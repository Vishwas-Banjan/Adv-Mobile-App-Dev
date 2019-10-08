import * as mongoose from 'mongoose';
import * as mongooseFloat from 'mongoose-float';

export const PaymentIntentSchema = new mongoose.Schema({
  paymentID: String,
  created: {
    type: Date,
    default: Date.now,
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
  successful: Boolean
});
