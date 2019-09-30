import { ApiModelProperty, ApiModelPropertyOptional } from '@nestjs/swagger';

export class CreateCreditCardDTO {
  @ApiModelPropertyOptional()
  customerId: string;

  @ApiModelProperty()
  number: string;

  @ApiModelProperty()
  expirationDate: string;

  @ApiModelProperty()
  cvv: string;
}
