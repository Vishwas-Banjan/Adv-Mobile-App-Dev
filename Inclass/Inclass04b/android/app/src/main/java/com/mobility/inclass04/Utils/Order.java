package com.mobility.inclass04.Utils;

import java.util.ArrayList;

public class Order {
    String orderId, orderTotal, orderTime;
    ArrayList<Product> itemsOrdered;

    public Order(String orderId, String orderTotal, String orderTime, ArrayList<Product> itemsOrdered) {
        this.orderId = orderId;
        this.orderTotal = orderTotal;
        this.orderTime = orderTime;
        this.itemsOrdered = itemsOrdered;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", orderTotal='" + orderTotal + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", itemsOrdered=" + itemsOrdered +
                '}';
    }

    public Order() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public ArrayList<Product> getItemsOrdered() {
        return itemsOrdered;
    }

    public void setItemsOrdered(ArrayList<Product> itemsOrdered) {
        this.itemsOrdered = itemsOrdered;
    }
}
