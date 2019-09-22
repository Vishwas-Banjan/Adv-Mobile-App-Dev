import {
  Body,
  Controller,
  Get,
  Put,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

import { User as UserDocument } from '../../types/user';
import { User } from 'src/utilities/user.decorator';
import { UpdateUserDTO } from 'src/dto/update-user.dto';
import { UserService } from 'src/shared/user.service';

@Controller('user')
export class UserController {
  constructor(private userService: UserService) {}

  @Get(':id')
  @UseGuards(AuthGuard())
  async getUserProfile(@User() user: UserDocument): Promise<UserDocument> {
    const { id } = user;
    return null;
  }

  @Put(':id')
  @UseGuards(AuthGuard())
  async update(
    @Body() product: UpdateUserDTO,
    @User() user: UserDocument,
  ): Promise<UserDocument> {
    const { id: userId } = user;
    return await this.userService.update(product, userId);
  }
}
