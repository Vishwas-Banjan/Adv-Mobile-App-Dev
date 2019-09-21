import { Body, Controller, Post, Get, ValidationPipe, UseGuards } from '@nestjs/common';

import { UserService } from '../shared/user.service';
import { Payload } from '../types/payload';
import { AuthService } from './auth.service';
import { RegisterDTO } from './dto/register.dto';
import { LoginDTO } from './dto/login.dto';
import { AuthGuard } from '@nestjs/passport';

@Controller('auth')
export class AuthController {
  constructor(
    private userService: UserService,
    private authService: AuthService,
  ) {}

  @Post('login')
  async login(@Body() userDTO: LoginDTO) {
    const user = await this.userService.findByLogin(userDTO);
    const payload: Payload = {
      username: user.username,
    };
    const token = await this.authService.signPayload(payload);
    return { user, token };
  }

  @Post('register')
  async register(@Body() userDTO: RegisterDTO) {
    // console.log(userDTO.username);
    const user = await this.userService.create(userDTO);
    const payload: Payload = {
      username: user.username,
    };
    const token = await this.authService.signPayload(payload);
    return { user, token };
  }

  // This route will require successfully passing our default auth strategy (JWT) in order
  // to access the route
  @Get('test')
  @UseGuards(AuthGuard('jwt'))
  testAuthRoute() {
    return {
      message: 'You did it!',
    };
  }
}
