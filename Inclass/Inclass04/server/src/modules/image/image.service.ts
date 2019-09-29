import { Injectable } from '@nestjs/common';
import { MongoGridFS, IGridFSWriteOption, IGridFSObject } from 'mongo-gridfs';
import { InjectConnection } from '@nestjs/mongoose';
import { Connection } from 'mongoose';
import { GridFSBucketReadStream } from 'mongodb';
import { DiskFile } from 'src/types/disk-file';

@Injectable()
export class ImageService {
  private fileModel: MongoGridFS;

  constructor(@InjectConnection() private readonly connection: Connection) {
    this.fileModel = new MongoGridFS(this.connection.db, 'images');
  }

  async readStream(id: string): Promise<GridFSBucketReadStream> {
    return this.fileModel.readFileStream(id);
  }

  async writeStream(stream, options?: IGridFSWriteOption): Promise<any> {
    return await this.fileModel
      .writeFileStream(stream, options)
      .then(ImageService.convertToFileInfo);
  }

  async findInfo(id): Promise<any> {
    return await this.fileModel
      .findById(id.toHexString())
      .then(ImageService.convertToFileInfo);
  }

  public async writeFile(file: DiskFile): Promise<any> {
    return await this.fileModel
      .uploadFile(
        file.path,
        {
          filename: file.originalname,
          contentType: file.mimetype,
        },
        true,
      )
      .then(ImageService.convertToFileInfo);
  }

  static convertToFileInfo(file: IGridFSObject): any {
    return file;
  }
}
