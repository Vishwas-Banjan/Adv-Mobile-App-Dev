import * as mongoose from 'mongoose';

export const ProductSchema = new mongoose.Schema({
  discount: Number,
  name: String,
  photo: String,
  price: Number,
  region: String
});
