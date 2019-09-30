import { ApiModelProperty, ApiModelPropertyOptional } from '@nestjs/swagger';

export class CreatePaymentDTO {
  @ApiModelPropertyOptional()
  customerId: string;

  @ApiModelProperty()
  paymentMethodNonce: string;
}
