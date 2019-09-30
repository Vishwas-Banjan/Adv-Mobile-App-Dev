import { Body, Controller, UseGuards, Post } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

import { User as UserDocument } from '../../types/user';
import { User } from '../../utilities/user.decorator';
import { CreatePaymentDTO } from '../../dto/create-payment.dto';
import { PaymentAccountService } from './../../shared/payment-account.service';
import { UserService } from './../../shared/user.service';
import { PaymentMethod } from './../../types/payment-method';

@Controller('paymentAccount')
export class PaymentAccountController {
  constructor(
    private payAccount: PaymentAccountService,
    private user: UserService,
  ) {}

  @Post('add')
  @UseGuards(AuthGuard())
  async addPayment(
    @Body() createPaymentDTO: CreatePaymentDTO,
    @User() user: UserDocument,
  ): Promise<PaymentMethod> {
    const { payAccId } = await this.user.findFromID(user.id);
    createPaymentDTO.customerId = payAccId;
    return await this.payAccount.addPayment(createPaymentDTO);
  }
}
