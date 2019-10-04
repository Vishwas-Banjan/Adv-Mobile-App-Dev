import {
  Controller,
  Post,
  UseInterceptors,
  UploadedFile,
  Get,
  Param,
  Body,
  Res,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { ImageService } from './image.service';
import { multerOptions } from './multer-options';

@Controller('image')
export class ImageController {
  constructor(private imageService: ImageService) {}

  @Post()
  @UseInterceptors(FileInterceptor('file', multerOptions))
  async upload(@UploadedFile() file): Promise<any> {
    return await this.imageService.writeFile(file);
  }

  @Get(':fileName')
  async getImage(
    @Param('fileName') fileName: string,
    @Res() res,
  ): Promise<any> {
    const file = await this.imageService.findFileByName(fileName);
    return file.pipe(res);
  }
}
