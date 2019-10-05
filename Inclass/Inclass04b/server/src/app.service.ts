import { Injectable } from '@nestjs/common';
import { InjectStripe } from "nestjs-stripe";
import * as Stripe from "stripe";

@Injectable()
export class AppService {
  getHello(): string {
    return 'Hello World!';
  }
}
