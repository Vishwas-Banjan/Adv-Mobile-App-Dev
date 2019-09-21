import { Document } from 'mongoose';

export interface User extends Document {
  username: string;
  readonly password: string;
  firstName: string;
  lastName: string;
  email: string;
  city: string;
  gender: string;
}
