import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import {CreatePaymentDTO} from '../dto/create-payment.dto';
import { InjectModel } from '@nestjs/mongoose';
import { InjectStripe } from 'nestjs-stripe';
// import Stripe from 'stripe';
import * as Stripe from "stripe";
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
    @InjectModel('ordersDB') private paymentDataModel: Model<PaymentIntent>
  ) {
    // console.info('Stripe client was loaded', this.stripeClient);
    StripeClient = stripeClient;
    // stripeClient = stripeClient.Stripe('sk_test_n9IezcfVX93Ymqds37cbIvOY00ubOw3Ytt')
  }

  async createCustomer(email: string): Promise<string> {
    // return customer id
    return await Stripe.customers.create({
      email: email,
    });
  }

  async createPaymentIntent(paymentIntentInfo: CreatePaymentDTO, user: User): Promise<object>{
    // save it to the database
    try {
      let price = paymentIntentInfo.products.reduce((acc, product) => {
        const price = product.price * product.quantity;
        return acc + price;
      }, 0);
      price = Number((price*100).toFixed(0));
      // console.log(typeof(price))

      const stripeIntent =  await StripeClient.paymentIntents.create({
        amount: price,  
        currency: paymentIntentInfo.currency,
        payment_method_types: [paymentIntentInfo.type],
      });
  
  
      
      await this.paymentDataModel.create(
        new PaymentIntentDTO(stripeIntent.id, user.id, stripeIntent.created, paymentIntentInfo.products, false, price));
      console.log(stripeIntent)
      return {
        "client_secret": stripeIntent.client_secret,
        "created": stripeIntent.created
      };
    } catch (error) {
      return error;
    }
  }

  // use webhooks if 

  async chargeAmount(stripePaymentToken: string): Promise<String>{
    const intent = await Stripe.paymentIntents.retrieve(stripePaymentToken);
    const charges = intent.charges.data;
    return null;
  }

  async createEphemeralKey(stripeVersion: string, customerID: string):Promise<String>{
    console.log(Stripe.ephemeralKeys.create(
      {customer: customerID},
      {stripe_version: stripeVersion}
    ));
    return await Stripe.ephemeralKeys.create(
      {customer: customerID},
      {stripe_version: stripeVersion}
    );
  }

  async validatePayment(
    rawBody: any,
    paymentValidationToken: PaymentValidationDTO
  ): Promise<any>{
    // return StripeClient.webhooks.constructEvent(
    //   rawBody,
    //   paymentValidationToken.stripeSignature,
    //   process.env.STRIPE_WEBHOOK_KEY,
    // ).then();
    return await this.savePaymentData(paymentValidationToken)
  }

  async savePaymentData(paymentValidationToken: PaymentValidationDTO){
    try {
      // console.log(paymentValidationToken);
      const {stripeId, type} = paymentValidationToken;
      console.log(type)
      if (
        type === "charge.succeeded" ||
        type === "charge.failed"
      ) {
        return await this.updateOrderDBs(
          (type === "charge_succeeded")? true: false,
          stripeId,
        );
      }
    } catch (error) {
      console.log("error: "+error)
      throw error;
    }
  }

  async updateOrderDBs(status, id): Promise<any>{
    // update status on db
    await this.paymentDataModel.updateOne( {"paymentID" : { $eq: id }},{ $set: { status: status }} );
    console.log("payment successful");
    return {"webhook":"done"};
  }
}
