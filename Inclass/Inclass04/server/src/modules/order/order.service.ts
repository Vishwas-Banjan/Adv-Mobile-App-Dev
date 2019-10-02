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
    const orders = await this.orderModel.find({ userId });

    if (!orders) {
      throw new HttpException('No Orders Found', HttpStatus.NO_CONTENT);
    }
    return orders;
  }

  async createOrder(orderDTO: CreateOrderDTO, userId: string, customerId) {
    if (!orderDTO.products || orderDTO.products.length === 0) {
      throw new HttpException('No Item Purchased', HttpStatus.BAD_REQUEST);
    }

    // calculate total
    const totalPrice = orderDTO.products.reduce((acc, product) => {
      const price = product.price * product.quantity;
      return acc + price;
    }, 0);

    if (totalPrice > 0) {
      // start transaction with braintree
      await this.braintreeProvider.sale({
        amount: totalPrice.toString(),
        paymentMethodNonce: orderDTO.paymentMethodNonce,
        customerId,
        options: {
          submitForSettlement: true,
        },
      });
    }

    // save order
    const createOrder = {
      userId,
      products: orderDTO.products,
      totalPrice,
    };
    const { _id } = await this.orderModel.create(createOrder);

    return { orderId: _id };
  }
}
