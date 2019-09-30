import { Controller, Get, UseGuards, Post, Body } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

import { User as UserDocument } from '../../types/user';
import { OrderService } from './order.service';
import { User } from './../../utilities/user.decorator';
import { CreateOrderDTO } from './../../dto/order.dto';

@Controller('order')
export class OrderController {
  constructor(private orderService: OrderService) {}

  @Get()
  @UseGuards(AuthGuard())
  listOrders(@User() { id }: UserDocument) {
    return this.orderService.listOrdersByUser(id);
  }

  @Post()
  @UseGuards(AuthGuard())
  createOrder(@Body() order: CreateOrderDTO, @User() user: UserDocument) {
    return this.orderService.createOrder(order, user.id);
  }
}
