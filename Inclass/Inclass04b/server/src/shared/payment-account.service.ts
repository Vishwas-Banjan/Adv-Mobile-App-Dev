import { Injectable } from '@nestjs/common';
import { PaymentMethod } from '../types/payment-method';
import {CreatePaymentDTO} from '../dto/create-payment.dto';
import { InjectStripe } from 'nestjs-stripe';
// import Stripe from 'stripe';
import * as Stripe from "stripe";
import { from } from 'rxjs';

let StripeClient;

@Injectable()
export class PaymentAccountService {
  
  public constructor(@InjectStripe() stripeClient: Stripe) {
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

  async createPaymentIntent(paymentIntentInfo: CreatePaymentDTO): Promise<object>{
    // console.log("started create payment service");
    return await StripeClient.paymentIntents.create({
      amount: Math.round(paymentIntentInfo.price*100),  // The price extracted from the endpoint parameters => 15€
      currency: paymentIntentInfo.currency,
      payment_method_types: [paymentIntentInfo.type], // The type extracted from the endpoint parameters => credit card
    });
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
    
  ): Promise<string>{
    return await Stripe.checkout.sessions.create({
      payment_method_types: ['card'],
      line_items: [{
        name: 'T-shirt',
        description: 'Comfortable cotton t-shirt',
        images: ['https://example.com/t-shirt.png'],
        amount: 500,
        currency: 'usd',
        quantity: 1,
      }],
      success_url: 'https://example.com/success?session_id={CHECKOUT_SESSION_ID}',
      cancel_url: 'https://example.com/cancel',
    });
  }

  async getClientToken(customerId): Promise<string> {
    return null;
  }

  async addPayment(creditCardDTO): Promise<PaymentMethod> {
    return null;
  }
}
