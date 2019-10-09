import { ApiModelProperty } from '@nestjs/swagger';

export class OrderProduct {
  @ApiModelProperty()
  product: string;

  @ApiModelProperty()
  name: string;

  @ApiModelProperty()
  quantity: number;

  @ApiModelProperty()
  price: number;
}
