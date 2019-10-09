import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import { OrderController } from './order.controller';
import { OrderService } from './order.service';
import { PassportModule } from '@nestjs/passport';
import { PaymentIntentSchema } from '../../models/payment.schema';
import { ProductService } from '../../modules/product/product.service';
import { SharedModule } from '../../shared/shared.module';
import { ProductSchema } from '../../models/product.schema';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'ordersDB', schema: PaymentIntentSchema }]),
    PassportModule.register({ defaultStrategy: 'jwt', session: false }),
    MongooseModule.forFeature([{ name: 'products', schema: ProductSchema }]),
    SharedModule,
  ],
  controllers: [OrderController],
  providers: [OrderService, ProductService],
})
export class OrderModule {}
