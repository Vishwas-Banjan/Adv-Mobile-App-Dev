import { Document } from 'mongoose';

interface ProductOrder {
    quantity: number;
    price: number;
    product: string;
    name: string;
}

export interface PaymentIntent extends Document {
    paymentID: String,
    personID: String,
    created: Date,
    products: ProductOrder[],
    successful: Boolean,
    price: Number
}
