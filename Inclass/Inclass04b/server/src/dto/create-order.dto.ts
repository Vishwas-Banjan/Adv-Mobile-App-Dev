import { ApiModelProperty, ApiModelPropertyOptional } from '@nestjs/swagger';
import { OrderProduct } from './order-product.dto';

export class CreateOrderDTO {
  @ApiModelProperty({ type: OrderProduct })
  products: OrderProduct[];

  @ApiModelProperty()
  paymentMethodNonce: string;

  @ApiModelPropertyOptional()
  user: string;
}
