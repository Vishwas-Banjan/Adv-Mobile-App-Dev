import { Injectable } from '@nestjs/common';
import { MongoGridFS, IGridFSWriteOption, IGridFSObject } from 'mongo-gridfs';
import { InjectConnection } from '@nestjs/mongoose';
import { Connection } from 'mongoose';
import { GridFSBucketReadStream, ObjectId } from 'mongodb';
import { DiskFile } from 'src/types/disk-file';

@Injectable()
export class ImageService {
  private fileModel: MongoGridFS;

  constructor(@InjectConnection() private readonly connection: Connection) {
    this.fileModel = new MongoGridFS(this.connection.db, 'images');
  }

  async readStream(id: ObjectId): Promise<GridFSBucketReadStream> {
    return await this.fileModel.readFileStream(id.toString());
  }

  async writeStream(
    stream,
    options?: IGridFSWriteOption,
  ): Promise<IGridFSObject> {
    return await this.fileModel.writeFileStream(stream, options);
  }

  async findFileByName(filename): Promise<GridFSBucketReadStream> {
    const { _id } = await this.fileModel.findOne({ filename });
    return await this.readStream(_id);
  }

  public async writeFile(file: DiskFile): Promise<IGridFSObject> {
    return await this.fileModel.uploadFile(
      file.path,
      {
        filename: file.originalname,
        contentType: file.mimetype,
      },
      true,
    );
  }
}
