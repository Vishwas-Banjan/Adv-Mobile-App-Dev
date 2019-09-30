import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import { OrderController } from './order.controller';
import { OrderService } from './order.service';
import { OrderSchema } from './../../models/order.schema';
import { BraintreeModule } from './../../braintree';
import { PassportModule } from '@nestjs/passport';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'orders', schema: OrderSchema }]),
    PassportModule.register({ defaultStrategy: 'jwt', session: false }),
    BraintreeModule.forFeature(),
  ],
  controllers: [OrderController],
  providers: [OrderService],
})
export class OrderModule {}
