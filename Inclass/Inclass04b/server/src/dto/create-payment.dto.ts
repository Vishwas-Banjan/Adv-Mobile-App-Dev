import { ApiModelProperty, ApiModelPropertyOptional } from '@nestjs/swagger';
import { IsNotEmpty } from 'class-validator';


export class CreatePaymentDTO {

  @IsNotEmpty()
  @ApiModelProperty()
  price: number;

  @ApiModelProperty()
  type: string;

  @ApiModelPropertyOptional()
  currency: string;
}
