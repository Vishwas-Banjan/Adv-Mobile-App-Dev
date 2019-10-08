import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import { OrderController } from './order.controller';
import { OrderService } from './order.service';
import { PassportModule } from '@nestjs/passport';
import { PaymentIntentSchema } from '../../models/payment.schema';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'ordersDB', schema: PaymentIntentSchema }]),
    PassportModule.register({ defaultStrategy: 'jwt', session: false }),
  ],
  controllers: [OrderController],
  providers: [OrderService],
})
export class OrderModule {}
