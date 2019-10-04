import { Module } from '@nestjs/common';
import { SharedModule } from './../../shared/shared.module';
import { ImageService } from './image.service';
import { ImageController } from './image.controller';

@Module({
  imports: [SharedModule],
  providers: [ImageService],
  controllers: [ImageController],
})
export class ImageModule {}
