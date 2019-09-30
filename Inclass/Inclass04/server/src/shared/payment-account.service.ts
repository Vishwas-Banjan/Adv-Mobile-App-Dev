import { BraintreeProvider, InjectBraintreeProvider } from './../braintree';
import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { PaymentMethod } from '../types/payment-method';

@Injectable()
export class PaymentAccountService {
  constructor(
    @InjectBraintreeProvider()
    private readonly braintreeProvider: BraintreeProvider,
  ) {}

  async createCustomer(): Promise<string> {
    const { success, customer } = await this.braintreeProvider.createCustomer(
      {},
    );
    if (!success) {
      throw new HttpException(
        'Fail to create payment account',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
    return customer.id;
  }

  async getClientToken(customerId): Promise<string> {
    const {
      success,
      clientToken,
    } = await this.braintreeProvider.generateClientToken(customerId);
    if (!success) {
      throw new HttpException(
        'Fail to generate client token',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
    return clientToken;
  }

  async addPayment(creditCardDTO): Promise<PaymentMethod> {
    const {
      success,
      paymentMethod,
    } = await this.braintreeProvider.createPayment(creditCardDTO);
    if (!success) {
      throw new HttpException(
        'Fail to add new payment',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
    return paymentMethod;
  }
}
