import { Body, Controller, Post, Get, UseGuards } from '@nestjs/common';

import { AuthService } from './auth.service';
import { AuthGuard } from '@nestjs/passport';
import { LoginDTO } from './../../dto/login.dto';
import { Payload } from './../../types/payload';
import { CreateUserDTO } from './../../dto/create-user.dto';
import { UserService } from './../../shared/user.service';

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
      email: user.email,
    };
    const token = await this.authService.signPayload(payload);
    return { user, token };
  }

  @Post('register')
  async register(@Body() userDTO: CreateUserDTO) {
    // console.log(userDTO.username);
    if(userDTO.email!=null && userDTO.password!=null && userDTO.firstName!=null){
      const user = await this.userService.create(userDTO);
    
      const payload: Payload = {
        email: user.email,
      };
      const token = await this.authService.signPayload(payload);
      return { user, token };
    }else{
      return {error: "Please provide all the necessary details, then only we could help you"}
    }
  }

  // This route will require successfully passing our default auth strategy (JWT) in order
  // to access the route
  @Get('test')
  @UseGuards(AuthGuard())
  testAuthRoute() {
    return {
      message: 'You did it!',
    };
  }
}
