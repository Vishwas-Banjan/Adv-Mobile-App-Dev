import { Body, Controller, Post, Get, UseGuards } from '@nestjs/common';

import { AuthService } from './auth.service';
import { AuthGuard } from '@nestjs/passport';
import { LoginDTO } from './../../dto/login.dto';
import { Payload } from './../../types/payload';
import { CreateUserDTO } from './../../dto/create-user.dto';
import { UserService } from './../../shared/user.service';
import { User } from './../../utilities/user.decorator';
import { User as UserDocument } from '../../types/user';

@Controller('auth')
export class AuthController {
  constructor(
    private userService: UserService,
    private authService: AuthService,
  ) {}

  @Post('login')
  async login(@Body() userDTO: LoginDTO) {
    const { user } = await this.userService.findByLogin(userDTO);
    const payload: Payload = {
      email: user.email,
      payAccId: user.payAccId,
    };
    const token = await this.authService.signPayload(payload);
    return { user, token };
  }

  @Post('register')
  async register(@Body() userDTO: CreateUserDTO) {
    // console.log(userDTO.username);
    const { user } = await this.userService.create(userDTO);

    const payload: Payload = {
      email: user.email,
      payAccId: user.payAccId,
    };
    const token = await this.authService.signPayload(payload);
    return { user, token };
  }

  // This route will require successfully passing our default auth strategy (JWT) in order
  // to access the route
  @Get('test')
  @UseGuards(AuthGuard())
  async testAuthRoute(@User() userDocument: UserDocument) {
    return await this.userService.findFromID(userDocument.id);
  }
}
