export interface CreateOrderDTO {
  products: Array<{
    productId: string;
    quantity: number;
    price: number;
  }>;
  paymentMethodNonce: string;
}
