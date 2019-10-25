import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Product } from './../../types/product';
import { FilterDTO } from '../../dto/filter.dto';
import { Filter } from '../../types/filter';

@Injectable()
export class ProductService {
  constructor(
    @InjectModel('products') private productModel: Model<Product>,
    @InjectModel('beacon_region_maps') private filterModel: Model<Filter>,
  ) {}

  async findAll(): Promise<Product[]> {
    return await this.productModel.find().populate('owner');
  }

  async filterProducts(filters: FilterDTO): Promise<object> {
    const filterInfo = await this.filterModel.findOne({
      minor: filters.minor,
      major: filters.major,
    });
    if (!filterInfo) {
      // major and minor are empty
      if (filters.region) {
        // filter by region
        return await this.findByRegion(filters.region);
      } else {
        // return all
        return await this.findAll();
      }
    }
    return await this.findByRegion(filterInfo.region);
  }

  async findByRegion(region: string): Promise<Product[]> {
    return await this.productModel.where('region', region).populate('owner');
  }

  async findById(id: string): Promise<Product> {
    const product = await this.productModel.findById(id).populate('owner');
    if (!product) {
      throw new HttpException('Product not found', HttpStatus.NO_CONTENT);
    }
    return product;
  }

  // async create(productDTO: CreateProductDTO, user: User): Promise<Product> {
  //   const product = await this.productModel.create({
  //     ...productDTO,
  //     owner: user,
  //   });
  //   await product.save();
  //   return product.populate('owner');
  // }

  // async update(
  //   id: string,
  //   productDTO: UpdateProductDTO,
  //   userId: string,
  // ): Promise<Product> {
  //   const product = await this.productModel.findById(id);
  //   if (userId !== product.owner.toString()) {
  //     throw new HttpException(
  //       'You do not own this product',
  //       HttpStatus.UNAUTHORIZED,
  //     );
  //   }
  //   await product.update(productDTO);
  //   return await this.productModel.findById(id).populate('owner');
  // }

  // async delete(id: string, userId: string): Promise<Product> {
  //   const product = await this.productModel.findById(id);
  //   if (userId !== product.owner.toString()) {
  //     throw new HttpException(
  //       'You do not own this product',
  //       HttpStatus.UNAUTHORIZED,
  //     );
  //   }
  //   await product.remove();
  //   return product.populate('owner');
  // }
}
