import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

import { Order } from '../../types/order';
import { CreateOrderDTO } from '../../dto/create-order.dto';

@Injectable()
export class OrderService {
  constructor(
    @InjectModel('orders') private orderModel: Model<Order>,
  ) {}

  async listOrdersByUser(userId: string) {
    const orders = await this.orderModel
      .find({ user: userId })
      .populate('products.product', { name: 1, photo: 1 });

    if (!orders) {
      throw new HttpException('No Orders Found', HttpStatus.NO_CONTENT);
    }
    return orders;
  }

  async createOrder(orderDTO: CreateOrderDTO, userId: string, customerId) {
    console.log(orderDTO);
    if (!orderDTO.products || orderDTO.products.length === 0) {
      throw new HttpException('No Item Purchased', HttpStatus.BAD_REQUEST);
    }

    // calculate total
    const totalPrice = orderDTO.products.reduce((acc, product) => {
      const price = product.price * product.quantity;
      return acc + price;
    }, 0);

    // save order
    const createOrder = {
      user: userId,
      products: orderDTO.products,
      totalPrice,
    };
    const { _id } = await this.orderModel.create(createOrder);

    return { orderId: _id };
  }
}
