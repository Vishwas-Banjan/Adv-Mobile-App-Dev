import * as mongoose from 'mongoose';

export const OrderSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'users',
  },
  totalPrice: {
    type: Number,
    default: 0,
  },
  products: [
    {
      productId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'products',
      },
      quantity: {
        type: Number,
        default: 1,
      },
      price: {
        type: Number,
        default: 1,
      },
    },
  ],
  created: {
    type: Date,
    default: Date.now,
  },
});
