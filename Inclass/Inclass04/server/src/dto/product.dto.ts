import { ApiModelProperty } from '@nestjs/swagger';

export class CreateProductDTO {
  @ApiModelProperty()
  title: string;

  @ApiModelProperty()
  image: string;

  @ApiModelProperty()
  description: string;

  @ApiModelProperty()
  price: number;
}

export type UpdateProductDTO = Partial<CreateProductDTO>;
