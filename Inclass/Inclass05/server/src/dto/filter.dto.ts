import { ApiModelProperty, ApiModelPropertyOptional } from '@nestjs/swagger';
import { OrderProduct } from './order-product.dto';

export class FilterDTO {
  @ApiModelProperty()
  minor: string;
  @ApiModelProperty()
  major: string;
  @ApiModelProperty()
  uuid: string;
}
