import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import * as bcrypt from 'bcrypt';
import { Model } from 'mongoose';
import { CreateUserDTO } from 'src/dto/create-user.dto';
import { User } from 'src/types/user';
import { LoginDTO } from 'src/dto/login.dto';
import { Payload } from 'src/types/payload';
import { UpdateUserDTO } from 'src/dto/update-user.dto';

@Injectable()
export class UserService {
  constructor(@InjectModel('users') private userModel: Model<User>) {}

  async create(userDTO: CreateUserDTO) {
    const { email } = userDTO;
    const user = await this.userModel.findOne({ email });
    if (user) {
      throw new HttpException('User already exists', HttpStatus.BAD_REQUEST);
    }

    const createdUser = new this.userModel(userDTO);
    await createdUser.save();
    return this.sanitizeUser(createdUser);
  }

  async find() {
    return await this.userModel.find();
  }

  async findFromID(id: string) {
    const user = await this.userModel.findById(id);
    if (!user) {
      throw new HttpException('user not found', HttpStatus.NOT_FOUND);
    }
    return user;
  }

  async findByLogin(userDTO: LoginDTO) {
    const { email, password } = userDTO;
    const user = await this.userModel
      .findOne({ email })
      .select('email password');
    if (!user) {
      throw new HttpException('Invalid credentials', HttpStatus.UNAUTHORIZED);
    }

    if (await bcrypt.compare(password, user.password)) {
      return this.sanitizeUser(user);
    } else {
      throw new HttpException('Invalid credentials', HttpStatus.UNAUTHORIZED);
    }
  }

  async findByPayload(payload: Payload) {
    const { email } = payload;
    return await this.userModel.findOne({ email });
  }

  async update(
    userDTO: UpdateUserDTO,
    id: string,
    userId: string,
  ): Promise<User> {
    if (userId !== id) {
      throw new HttpException('Not authorized', HttpStatus.UNAUTHORIZED);
    }
    const user = await this.userModel.findById(id);
    await user.updateOne(userDTO);
    return await this.userModel.findById(id);
  }

  sanitizeUser(user: User) {
    const sanitized = user.toObject();
    delete sanitized.password;
    return sanitized;
    // return user.depopulate('password');
  }
}
