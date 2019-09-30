import { Body, Controller, UseGuards, Post } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

import { User as UserDocument } from '../../types/user';
import { User } from '../../utilities/user.decorator';
import { CreateCreditCardDTO } from './../../dto/create-credit-card.dto';
import { PaymentAccountService } from 'src/shared/payment-account.service';
import { CreditCard } from './../../types/credit-card';
import { UserService } from './../../shared/user.service';

@Controller('paymentAccount')
export class PaymentAccountController {
  constructor(private payAccount: PaymentAccountService,
    private user: UserService) {}

  @Post()
  @UseGuards(AuthGuard())
  async addCreditCard(@Body() creditCardDTO: CreateCreditCardDTO, @User() user: UserDocument): Promise<CreditCard> {
    const {payAccId} = await this.user.findFromID(user.id);
    creditCardDTO.customerId = payAccId;
    return await this.payAccount.createCreditCard(creditCardDTO);
  }
}
