import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import { AppController } from './app.controller';
import { AppService } from './app.service';
import { SharedModule } from './shared/shared.module';
import { AuthModule } from './modules/auth/auth.module';
import { UserModule } from './modules/user/user.module';

process.env.MONGO_URI = "mongodb+srv://rootuser:mobilityUser1@mobility-dvxsj.mongodb.net/users?retryWrites=true&w=majority";

@Module({
  imports: [
    MongooseModule.forRoot(process.env.MONGO_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    }),
    SharedModule,
    UserModule,
    AuthModule,
    // ProductModule,
    // OrderModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
