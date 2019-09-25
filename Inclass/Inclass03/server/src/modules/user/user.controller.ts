import { Body, Controller, Get, Put, UseGuards, Param } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

import { User as UserDocument } from './../../types/user';
import { User } from './../../utilities/user.decorator';
import { UpdateUserDTO } from './../../dto/update-user.dto';
import { UserService } from './../../shared/user.service';

@Controller('user')
export class UserController {
  constructor(private userService: UserService) {}

  @Get(':id')
  @UseGuards(AuthGuard())
  async getUserProfileById(@Param('id') id: string): Promise<UserDocument> {
    return await this.userService.findFromID(id);
  }

  @Get('email/:email')
  @UseGuards(AuthGuard())
  async getUserProfileByEmail(@Param('email') email: string): Promise<UserDocument> {
    return await this.userService.findByPayload({email: email});
  }

  @Put(':id')
  @UseGuards(AuthGuard())
  async update(
    @Body() product: UpdateUserDTO,
    @Param('id') id: string,
    @User() user: UserDocument,
  ): Promise<UserDocument> {
    const { id: userId } = user;
    return await this.userService.update(product, id, userId);
  }
}
