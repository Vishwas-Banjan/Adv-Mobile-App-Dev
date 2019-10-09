import { ApiModelProperty, ApiModelPropertyOptional } from '@nestjs/swagger';
import { IsNotEmpty } from 'class-validator';
import { OrderProduct } from './order-product.dto';

export class CreatePaymentDTO {
  @ApiModelProperty()
  type: string;

  @ApiModelPropertyOptional()
  currency: string;

  @ApiModelProperty({ type: OrderProduct })
  products: OrderProduct[];
}
