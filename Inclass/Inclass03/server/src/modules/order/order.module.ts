import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import { OrderController } from './order.controller';
import { OrderService } from './order.service';
import { SharedModule } from './../../shared/shared.module';
import { OrderSchema } from './../../models/order.schema';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'Order', schema: OrderSchema }]),
    SharedModule,
  ],
  controllers: [OrderController],
  providers: [OrderService],
})
export class OrderModule {}
