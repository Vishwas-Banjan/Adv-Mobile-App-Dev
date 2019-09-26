import { ApiModelPropertyOptional, ApiModelProperty } from '@nestjs/swagger';

export class UpdateUserDTO {
  @ApiModelPropertyOptional()
  firstName: string;

  @ApiModelPropertyOptional()
  lastName: string;

  @ApiModelPropertyOptional()
  city: string;

  @ApiModelPropertyOptional()
  gender: string;

  @ApiModelProperty()
  id: string;
}
