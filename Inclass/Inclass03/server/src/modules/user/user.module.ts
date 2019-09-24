import { Module } from '@nestjs/common';

import { UserController } from './user.controller';
import { SharedModule } from './../../shared/shared.module';
import { PassportModule } from '@nestjs/passport';

@Module({
  imports: [
    SharedModule,
    PassportModule.register({ defaultStrategy: 'jwt', session: false }),
  ],
  controllers: [UserController],
})
export class UserModule {}
