import { Module } from '@nestjs/common';

import { SharedModule } from '../../shared/shared.module';
import { PassportModule } from '@nestjs/passport';
import { PaymentAccountController } from './payment-account.controller';

@Module({
  imports: [
    SharedModule,
    PassportModule.register({ defaultStrategy: 'jwt', session: false }),
  ],
  controllers: [PaymentAccountController],
})
export class PaymentAccountModule {}
