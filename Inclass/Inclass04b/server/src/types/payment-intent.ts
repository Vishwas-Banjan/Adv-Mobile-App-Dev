import { Document } from 'mongoose';

interface ProductOrder {
    quantity: number;
    price: number;
    product: string;
}

export interface PaymentIntent extends Document {
    paymentID: String,
    created: Date,
    products: ProductOrder[],
    successful: Boolean
}
