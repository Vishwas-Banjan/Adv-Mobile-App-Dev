import { Body, Controller, UseGuards, Post, Get, Req, Header } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

import { User as UserDocument } from '../../types/user';
import { User } from '../../utilities/user.decorator';
import { CreatePaymentDTO } from '../../dto/create-payment.dto';
import { PaymentAccountService } from './../../shared/payment-account.service';
import { PaymentMethod } from './../../types/payment-method';
import { PaymentValidationDTO } from 'src/dto/payment-validation.dto';

@Controller('paymentAccount')
export class PaymentAccountController {
  constructor(
    private payAccount: PaymentAccountService,
    // private user: UserService,
  ) {}

  @Post()
  @UseGuards(AuthGuard())
  async createPaymentIntent(@Body() createPaymentDTO: CreatePaymentDTO): Promise<object>{
    // create order history and send client secret
    // console.log("hit create payment intent "+ this.payAccount.createPaymentIntent(createPaymentDTO))
    // this.payAccount = new PaymentAccountService();
    return this.payAccount.createPaymentIntent(createPaymentDTO);
    // return await this.payAccount.createPaymentIntent(createPaymentDTO);
  }

  @Post('validate')
  @Header("stripe-signature", "string")
  @UseGuards(AuthGuard())
  async validatePayment(
    @Req() {rawBody},
    @Body() requestBody,
    signature: string
  ): Promise<PaymentMethod> {
    // create order history and send client secret
    // createPaymentDTO.customerId = user.payAccId;
    // return await this.payAccount.validatePayment(createPaymentDTO);
    // return await this.payAccount.validatePayment({
    //   stripeSignature
    //   stripeResponse: rawBody,
    //   type: requestBody.type,  
    //   stripeId: requestBody.data.object.id,
    // });
    return
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
