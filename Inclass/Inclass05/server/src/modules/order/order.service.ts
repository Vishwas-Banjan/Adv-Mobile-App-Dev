import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { PaymentIntent } from '../../types/payment-intent';
import { ProductService } from '../product/product.service';

@Injectable()
export class OrderService {
  constructor(
    @InjectModel('ordersDB') private paymentDataModel: Model<PaymentIntent>,
  ) {}

  async listOrdersByUser(userId: string) {
    const orders = await this.paymentDataModel.find({
      personID: { $eq: userId },
    });

    if (!orders || orders.length == 0) {
      throw new HttpException('No Orders Found', HttpStatus.NO_CONTENT);
    }

    let result: Array<object> = [];
    orders.map(order => {
      const productData: Array<object> = [];
      result.push({
        paymentID: order._id,
        created: order.created,
        products: order.products,
        successful: order.successful,
        price: order.price,
      });
      return result;
    });
    return result;
  }

  // async createOrder(orderDTO: CreateOrderDTO, userId: string, customerId) {
  //   console.log(orderDTO);
  //   if (!orderDTO.products || orderDTO.products.length === 0) {
  //     throw new HttpException('No Item Purchased', HttpStatus.BAD_REQUEST);
  //   }

  //   // calculate total
  //   const totalPrice = orderDTO.products.reduce((acc, product) => {
  //     const price = product.price * product.quantity;
  //     return acc + price;
  //   }, 0);

  //   // save order
  //   const createOrder = {
  //     user: userId,
  //     products: orderDTO.products,
  //     totalPrice,
  //   };
  //   const { _id } = await this.orderModel.create(createOrder);

  //   return { orderId: _id };
  // }
}
