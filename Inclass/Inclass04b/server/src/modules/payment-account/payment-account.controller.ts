import {
  Body,
  Controller,
  UseGuards,
  Post,
  Get,
  Req,
  Header,
  Headers,
  HttpException,
  HttpStatus,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

import { User as UserDocument } from '../../types/user';
import { User } from '../../utilities/user.decorator';
import { CreatePaymentDTO } from '../../dto/create-payment.dto';
import { PaymentAccountService } from './../../shared/payment-account.service';
import { userInfo } from 'os';

@Controller('paymentAccount')
export class PaymentAccountController {
  constructor(private payAccount: PaymentAccountService) {}

  @Post()
  @UseGuards(AuthGuard())
  async createPaymentIntent(
    @Body() createPaymentDTO: CreatePaymentDTO,
    @User() user: UserDocument,
  ): Promise<object> {
    if (!createPaymentDTO.products) {
      throw new HttpException('No Orders Found', HttpStatus.NO_CONTENT);
    }
    return this.payAccount.createPaymentIntent(createPaymentDTO, user);
  }

  // url: http://localhost:8080/api/paymentAccount/validate

  @Post('validate')
  async validatePayment(
    @Req() rawBody,
    @Body() body,
    @User() user,
    @Headers('stripe-signature') signature: string,
  ): Promise<void> {
    console.log(JSON.stringify(body));
    return this.payAccount.validatePayment({
      paymentIntent: body.object.payment_intent,
      stripeResponse: rawBody,
      type: body.type,
      stripeId: body.data.object.id,
      paymentMethod: body.object.payment_method,
      customerId: user.payAccId,
    });
  }

  // @Get('clientToken')
  // @UseGuards(AuthGuard())
  // async getClientToken(@User() user: UserDocument) {
  //   const clientToken = await this.payAccount.getClientToken({
  //     customerId: user.payAccId,
  //   });
  //   return { clientToken };
  // }
}
