import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import { AppController } from './app.controller';
import { AppService } from './app.service';
import { SharedModule } from './shared/shared.module';
import { AuthModule } from './modules/auth/auth.module';
import { UserModule } from './modules/user/user.module';

import { ProductModule } from './modules/product/product.module';
import { ImageModule } from './modules/image/image.module';
import { OrderModule } from './modules/order/order.module';
import { PaymentAccountModule } from './modules/payment-account/payment-account.module';

@Module({
  imports: [
    MongooseModule.forRoot(process.env.MONGO_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    }),
    SharedModule,
    UserModule,
    AuthModule,
    ProductModule,
    ImageModule,
    OrderModule,
    PaymentAccountModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
