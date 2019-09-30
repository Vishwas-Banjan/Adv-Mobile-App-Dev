import { ApiModelProperty } from '@nestjs/swagger';

export class OrderProduct {
  @ApiModelProperty()
  productId: string;

  @ApiModelProperty()
  quantity: number;

  @ApiModelProperty()
  price: number;
}
