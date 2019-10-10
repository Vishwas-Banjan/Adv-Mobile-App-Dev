import * as mongoose from 'mongoose';
import * as mongooseFloat from 'mongoose-float';

export const OrderSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'users',
  },
  totalPrice: {
    type: mongooseFloat.loadType(mongoose),
    default: 0,
  },
  products: [
    {
      product: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'products',
      },
      quantity: {
        type: Number,
        default: 1,
      },
      price: {
        type: mongooseFloat.loadType(mongoose),
        default: 1,
      },
    },
  ],
  created: {
    type: Date,
    default: Date.now,
  },
});
