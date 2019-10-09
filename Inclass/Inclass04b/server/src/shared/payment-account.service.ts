import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { CreatePaymentDTO } from '../dto/create-payment.dto';
import { InjectModel } from '@nestjs/mongoose';
import { InjectStripe } from 'nestjs-stripe';
// import Stripe from 'stripe';
import * as Stripe from 'stripe';
import { from } from 'rxjs';
import { Model } from 'mongoose';
import { PaymentIntent } from '../types/payment-intent';
import { PaymentIntentDTO } from '../dto/payment-intent.dto';
import { User } from '../types/user';
import { PaymentValidationDTO } from '../dto/payment-validation.dto';

let StripeClient;

@Injectable()
export class PaymentAccountService {
  public constructor(
    @InjectStripe() stripeClient: Stripe,
    @InjectModel('users') private userModel: Model<User>,
    @InjectModel('ordersDB') private paymentDataModel: Model<PaymentIntent>,
  ) {
    // console.info('Stripe client was loaded', this.stripeClient);
    StripeClient = stripeClient;
    // stripeClient = stripeClient.Stripe('sk_test_n9IezcfVX93Ymqds37cbIvOY00ubOw3Ytt')
  }

  async createCustomer(email: string): Promise<string> {
    // return customer id
    const customer = await StripeClient.customers.create({
      email,
    });
    return customer.id;
  }

  async createPaymentIntent(
    paymentIntentInfo: CreatePaymentDTO,
    user: User,
  ): Promise<object> {
    // save it to the database
    try {
      let price = paymentIntentInfo.products.reduce((acc, product) => {
        const price = product.price * product.quantity;
        return acc + price;
      }, 0);
      price = Number((price * 100).toFixed(0));
      // console.log(typeof(price))

      const paymentMethod = await this.getPaymentMethod(user.payAccId);
      let stripeIntent = null;
      if (paymentMethod) {
        stripeIntent = await StripeClient.paymentIntents.create({
          amount: price,
          currency: paymentIntentInfo.currency,
          payment_method_types: [paymentIntentInfo.type],
          customer: user.payAccId,
          payment_method: paymentMethod,
        });
      } else {
        stripeIntent = await StripeClient.paymentIntents.create({
          amount: price,
          currency: paymentIntentInfo.currency,
          payment_method_types: [paymentIntentInfo.type],
        });
      }

      await this.paymentDataModel.create(
        new PaymentIntentDTO(
          stripeIntent.id,
          user.id,
          stripeIntent.created,
          paymentIntentInfo.products,
          false,
          price,
        ),
      );
      // console.log(stripeIntent);
      return {
        client_secret: stripeIntent.client_secret,
        created: stripeIntent.created,
      };
    } catch (error) {
      return error;
    }
  }

  // use webhooks if

  async chargeAmount(stripePaymentToken: string): Promise<String> {
    const intent = await Stripe.paymentIntents.retrieve(stripePaymentToken);
    const charges = intent.charges.data;
    return null;
  }

  async getPaymentMethod(customerId) {
    const paymentMethod = await StripeClient.paymentMethods.list({
      customer: customerId,
      type: 'card',
    });

    return paymentMethod.data.length > 0 ? paymentMethod.data[0].id : null;
  }

  async createEphemeralKey(
    stripeVersion: string,
    customerID: string,
  ): Promise<String> {
    console.log(
      Stripe.ephemeralKeys.create(
        { customer: customerID },
        { stripe_version: stripeVersion },
      ),
    );
    return await Stripe.ephemeralKeys.create(
      { customer: customerID },
      { stripe_version: stripeVersion },
    );
  }

  async validatePayment(
    paymentValidationToken: PaymentValidationDTO,
  ): Promise<any> {
    // check if the api request is made by stripe
    if (
      StripeClient.webhooks.constructEvent(
        paymentValidationToken.stripeResponse,
        paymentValidationToken.stripeId,
        process.env.STRIPE_WEBHOOK_KEY,
      )
    ) {
      return await this.savePaymentData(paymentValidationToken);
    } else {
      throw new HttpException('No Orders Found', HttpStatus.CONFLICT);
    }
  }

  async savePaymentData(token: PaymentValidationDTO) {
    try {
      console.log(token.type);
      if (token.type === 'charge.succeeded' || token.type === 'charge.failed') {
        // attach payment method to customer
        await this.savePaymentMethod(token.customerId, token.paymentMethod);
        return await this.updateOrderDBs(
          token.type === 'charge.succeeded' ? true : false,
          token.paymentIntent,
        );
      }
    } catch (error) {
      console.log('error: ' + error);
      throw error;
    }
  }

  async savePaymentMethod(customerID, paymentMethod) {
    try {
      return await StripeClient.paymentMethods.attach(paymentMethod, {
        customer: customerID,
      });
    } catch (error) {
      console.log('error: ' + error);
      throw error;
    }
  }

  async updateOrderDBs(status, id): Promise<any> {
    // update status on db
    await this.paymentDataModel
      .findOne({ paymentID: id })
      .update({ successful: status }, () => {
        return { webhook: 'done' };
      });
    // console.log("payment successful");
  }
}
