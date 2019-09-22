import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

import { Product } from '../types/product';
import { User } from '../types/user';
import { CreateUserDTO, UpdateUserDTO } from './user.dto';

@Injectable()
export class UserService {
  constructor(@InjectModel('User') private userModel: Model<User>) {}

  async findById(id: string): Promise<User> {
    const userData = await this.userModel.findById(id);
    if (!userData) {
      throw new HttpException('Product not found', HttpStatus.NO_CONTENT);
    }
    return userData;
  }

  async update(
    id: string,
    userData: UpdateUserDTO,
  ): Promise<User> {
    const userValid = await this.userModel.findById(id);
    if (id !== userValid.id) {
      throw new HttpException(
        'You are not authorized to see this information',
        HttpStatus.UNAUTHORIZED,
      );
    }
    await userValid.update(userData);
    return await this.userModel.findById(id).populate('owner');
  }

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
