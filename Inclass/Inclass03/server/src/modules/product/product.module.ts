import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { ProductController } from './product.controller';
import { ProductService } from './product.service';
import { SharedModule } from 'src/shared/shared.module';
import { ProductSchema } from 'src/models/product.schema';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'Product', schema: ProductSchema }]),
    SharedModule,
  ],
  providers: [ProductService],
  controllers: [ProductController],
})
export class ProductModule {}
