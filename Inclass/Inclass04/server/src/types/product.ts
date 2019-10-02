import { Document } from 'mongoose';

export interface Product extends Document {
  discount: number;
  name: string;
  photo: string;
  price: number;
  region: string;
}
