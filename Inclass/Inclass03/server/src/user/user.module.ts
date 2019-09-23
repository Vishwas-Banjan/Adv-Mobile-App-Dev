import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

// import { ProductSchema } from '../models/product.schema';
import { SharedModule } from '../shared/shared.module';
import { UserController } from './user.controller';
// import { ProductService } from './user.service';
import { UserSchema } from 'src/models/user.schema';
import { UserService } from './user.service';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'user', schema: UserSchema }]),
    SharedModule,
  ],
  providers: [UserService],
  controllers: [UserController],
})
export class UserModule {}
