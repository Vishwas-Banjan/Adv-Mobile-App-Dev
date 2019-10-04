import { Body, Controller, UseGuards, Post, Get } from '@nestjs/common';
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
    createPaymentDTO.customerId = user.payAccId;
    return await this.payAccount.addPayment(createPaymentDTO);
  }

  @Get('clientToken')
  @UseGuards(AuthGuard())
  async getClientToken(@User() user: UserDocument) {
    const clientToken = await this.payAccount.getClientToken({
      customerId: user.payAccId,
    });
    return { clientToken };
  }
}
