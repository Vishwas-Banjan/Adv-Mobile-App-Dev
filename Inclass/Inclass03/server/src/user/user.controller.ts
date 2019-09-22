import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  Put,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

import { SellerGuard } from '../guards/seller.guard';
// import { Product } from '../types/product';
import { User as UserDocument } from '../types/user';
import { UserService } from './user.service';

@Controller('user')
export class UserController {
  constructor(private userService: UserService) {}

  @Get(':id')
  @UseGuards(AuthGuard('jwt'), SellerGuard)
  async getUserData(@Param('id') id: string): Promise<UserDocument> {
    return await this.userService.findById(id);
  }

  @Post()
  @UseGuards(AuthGuard('jwt'), SellerGuard)
  async create(
    @Body() user: UserDocument,
    @Param('id') id: string,
  ): Promise<UserDocument> {
    return await this.userService.update(id, user);
  }

  // @Delete(':id')
  // @UseGuards(AuthGuard('jwt'), SellerGuard)
  // async delete(
  //   @Param('id') id: string,
  //   @User() user: UserDocument,
  // ): Promise<Product> {
  //   const { id: userId } = user;
  //   return await this.userService.delete(id, userId);
  // }
}
