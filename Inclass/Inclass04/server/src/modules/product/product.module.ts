import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { ProductController } from './product.controller';
import { ProductService } from './product.service';
import { SharedModule } from './../../shared/shared.module';
import { ProductSchema } from './../../models/product.schema';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'products', schema: ProductSchema }]),
    SharedModule,
  ],
  providers: [ProductService],
  controllers: [ProductController],
})
export class ProductModule {}
