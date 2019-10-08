import { OrderProduct } from "./order-product.dto";

export class PaymentIntentDTO {

     constructor (
         public paymentID: string, 
         public personID: string, 
         public created: Date, 
         public products: OrderProduct[],
         public successful: boolean){}
}
