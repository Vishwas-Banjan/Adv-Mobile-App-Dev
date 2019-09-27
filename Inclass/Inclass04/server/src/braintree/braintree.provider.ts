import { Injectable, Inject } from '@nestjs/common';
import { BraintreeOptions } from './interfaces';
import * as braintree from 'braintree';
import { BRAINTREE_OPTIONS_PROVIDER } from './braintree.constants';

@Injectable()
export default class BraintreeProvider {
  protected readonly gateway: braintree.BraintreeGateway;

  constructor(@Inject(BRAINTREE_OPTIONS_PROVIDER) options: BraintreeOptions) {
    this.gateway = new braintree.BraintreeGateway(options);
  }

  async createCustomer(
    request: braintree.CustomerCreateRequest,
  ): Promise<braintree.ValidatedResponse<braintree.Customer>> {
    return await this.gateway.customer.create(request);
  }

  async generateClientToken(
    request: braintree.ClientTokenRequest,
  ): Promise<braintree.ValidatedResponse<braintree.ClientToken>> {
    return await this.gateway.clientToken.generate(request);
  }
}
