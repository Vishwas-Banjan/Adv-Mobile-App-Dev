import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import * as bcrypt from 'bcrypt';
import { Model } from 'mongoose';
import { CreateUserDTO } from './../dto/create-user.dto';
import { User } from './../types/user';
import { LoginDTO } from './../dto/login.dto';
import { Payload } from './../types/payload';
import { UpdateUserDTO } from './../dto/update-user.dto';
import { PaymentAccountService } from './payment-account.service';

@Injectable()
export class UserService {
  constructor(
    @InjectModel('users') private userModel: Model<User>,
    private paymentAccount: PaymentAccountService,
  ) {}

  async create(userDTO: CreateUserDTO): Promise<{ user: User }> {
    // find duplicates
    const { email } = userDTO;
    const foundUser = await this.userModel.findOne({ email });
    if (foundUser) {
      throw new HttpException('User already exists', HttpStatus.BAD_REQUEST);
    }

    // create payment account
    const payAccId = await this.paymentAccount.createCustomer(email);
    userDTO.payAccId = payAccId;

    let createdUser = new this.userModel(userDTO);

    // save user to db
    await createdUser.save();

    // remove password field
    createdUser = this.sanitizeUser(createdUser);

    this.paymentAccount.createCustomer(createdUser.email);

    return { user: createdUser };
  }

  async find() {
    return await this.userModel.find();
  }

  async findFromID(id: string) {
    const user = await this.userModel.findById(id);
    if (!user) {
      throw new HttpException('user not found', HttpStatus.NOT_FOUND);
    }
    return this.sanitizeUser(user);
  }

  async findByLogin(userDTO: LoginDTO) {
    // find user by email
    const { email, password } = userDTO;
    const userModel = await this.userModel
      .findOne({ email })
      .select('email password payAccId firstName lastName city');
    if (!userModel) {
      throw new HttpException('Invalid credentials', HttpStatus.UNAUTHORIZED);
    }

    let user = null;
    // compare password
    if (await bcrypt.compare(password, userModel.password)) {
      user = this.sanitizeUser(userModel);
    } else {
      throw new HttpException('Invalid credentials', HttpStatus.UNAUTHORIZED);
    }

    return { user };
  }

  async findByPayload(payload: Payload) {
    const { email } = payload;
    return await this.userModel.findOne({ email });
  }

  async update(id: string, userDTO: UpdateUserDTO): Promise<User> {
    const user = await this.userModel.findById(id);
    if (!user) {
      throw new HttpException('Not authorized', HttpStatus.UNAUTHORIZED);
    }
    await user.updateOne(userDTO);
    return await this.userModel.findById(id);
  }

  sanitizeUser(user: User) {
    const sanitized = user.toObject();
    delete sanitized.password;
    return sanitized;
  }
}
