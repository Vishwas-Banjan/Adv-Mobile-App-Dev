import { Injectable } from '@nestjs/common';
import { PaymentMethod } from '../types/payment-method';

@Injectable()
export class PaymentAccountService {
  constructor() {}

  async createCustomer(): Promise<string> {
    return null;
  }

  async getClientToken(customerId): Promise<string> {
    return null;
  }

  async addPayment(creditCardDTO): Promise<PaymentMethod> {
    return null;
  }
}
