import { ApiModelProperty } from '@nestjs/swagger';

export class LoginDTO {
  @ApiModelProperty()
  username: string;

  @ApiModelProperty()
  password: string;
}
