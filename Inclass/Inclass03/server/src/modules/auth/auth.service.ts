import { Injectable } from '@nestjs/common';

import { JwtService } from '@nestjs/jwt';
import { Payload } from 'src/types/payload';
import { UserService } from 'src/shared/user.service';

@Injectable()
export class AuthService {
  constructor(
    private userService: UserService,
    private jwtService: JwtService,
  ) {}

  async signPayload(payload: Payload) {
    return this.jwtService.sign(payload);
  }

  async validateUser(payload: Payload) {
    return await this.userService.findByPayload(payload);
  }
}
