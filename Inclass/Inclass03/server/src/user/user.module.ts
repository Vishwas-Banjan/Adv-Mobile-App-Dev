import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import { ProductSchema } from '../models/product.schema';
import { SharedModule } from '../shared/shared.module';
import { UserController } from './user.controller';
import { ProductService } from './user.service';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'Product', schema: ProductSchema }]),
    SharedModule,
  ],
  providers: [ProductService],
  controllers: [UserController],
})
export class ProductModule {}
