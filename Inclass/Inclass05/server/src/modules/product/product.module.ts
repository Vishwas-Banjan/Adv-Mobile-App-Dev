import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { ProductController } from './product.controller';
import { ProductService } from './product.service';
import { SharedModule } from './../../shared/shared.module';
import { ProductSchema } from './../../models/product.schema';
import { FilterSchema } from '../../models/filter.shcema';
@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'products', schema: ProductSchema }]),
    MongooseModule.forFeature([
      { name: 'beacon_region_maps', schema: FilterSchema },
    ]),
    SharedModule,
  ],
  providers: [ProductService],
  controllers: [ProductController],
})
export class ProductModule {}
