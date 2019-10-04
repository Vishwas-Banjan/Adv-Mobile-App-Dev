import { Module } from '@nestjs/common';
import { APP_FILTER, APP_INTERCEPTOR } from '@nestjs/core';
import { MongooseModule } from '@nestjs/mongoose';

import { UserSchema } from '../models/user.schema';
import { HttpExceptionFilter } from './http-exception.filter';
import { LoggingInterceptor } from './logging.interceptor';
import { UserService } from './user.service';

import { PaymentAccountService } from './payment-account.service';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'users', schema: UserSchema }])
  ],
  providers: [
    UserService,
    PaymentAccountService,
    {
      provide: APP_FILTER,
      useClass: HttpExceptionFilter,
    },
    {
      provide: APP_INTERCEPTOR,
      useClass: LoggingInterceptor,
    },
  ],
  exports: [UserService, PaymentAccountService],
})
export class SharedModule {}
