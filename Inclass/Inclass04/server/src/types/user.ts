import { Document } from 'mongoose';

export interface User extends Document {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  email: string;
  city: string;
  gender: string;
  payAccId: string;
}
