import { Controller, Get, UseGuards, Post, Body } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

import { User as UserDocument } from '../../types/user';
import { OrderService } from './order.service';
import { User } from './../../utilities/user.decorator';

@Controller('order')
export class OrderController {
  constructor(private orderService: OrderService) {}

  @Get()
  @UseGuards(AuthGuard())
  listOrders(@User() user: UserDocument) {
    console.log(user.email);
    return this.orderService.listOrdersByUser(user.id);
  }

  // @Post()
  // @UseGuards(AuthGuard())
  // createOrder(@Body() order: CreateOrderDTO, @User() user: UserDocument) {
  //   return this.orderService.createOrder(order, user.id, user.payAccId);
  // }
}
