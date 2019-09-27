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

  @Put()
  @UseGuards(AuthGuard())
  async update(
    @Body() userDTO: UpdateUserDTO,
    @User() user: UserDocument,
  ): Promise<UserDocument> {
    return await this.userService.update(user.id, userDTO);
  }
}
