import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

import { Order } from '../../types/order';
import { CreateOrderDTO } from './../../dto/order.dto';
import { InjectBraintreeProvider, BraintreeProvider } from './../../braintree';

@Injectable()
export class OrderService {
  constructor(
    @InjectModel('orders') private orderModel: Model<Order>,
    @InjectBraintreeProvider()
    private readonly braintreeProvider: BraintreeProvider,
  ) {}

  async listOrdersByUser(userId: string) {
    const orders = await this.orderModel
      .find({ owner: userId })
      .populate('owner')
      .populate('products.product');

    if (!orders) {
      throw new HttpException('No Orders Found', HttpStatus.NO_CONTENT);
    }
    return orders;
  }

  async createOrder(orderDTO: CreateOrderDTO, userId: string) {
    // save order
    const createOrder = {
      userId,
      products: orderDTO.products,
    };
    const { _id } = await this.orderModel.create(createOrder);
    let order = await this.orderModel
      .findById(_id)
      .populate('products.product');

    // calculate total
    const totalPrice = order.products.reduce((acc, product) => {
      const price = product.price * product.quantity;
      return acc + price;
    }, 0);
    await order.updateOne({ totalPrice });

    // start transaction with braintree
    this.braintreeProvider.sale({
      amount: totalPrice.toString(),
      paymentMethodNonce: orderDTO.paymentMethodNonce,
      options: {
        submitForSettlement: true,
      },
    });

    // get order to return
    order = await this.orderModel
      .findById(_id)
      .populate('user')
      .populate('products.product');
    return order;
  }
}
