import { Document } from 'mongoose';

export interface Product extends Document {
  discount: Number;
  name: String;
  photo: String;
  price: Number;
  region: String;
}
