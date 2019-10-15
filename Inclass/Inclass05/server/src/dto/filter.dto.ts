import { ApiModelProperty, ApiModelPropertyOptional } from '@nestjs/swagger';

export class FilterDTO {
  @ApiModelPropertyOptional()
  minor: string;
  @ApiModelPropertyOptional()
  major: string;
  @ApiModelPropertyOptional()
  uuid: string;
  @ApiModelPropertyOptional()
  region: string;
}
