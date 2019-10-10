package com.mobility.inclass04.Utils;

import java.util.ArrayList;

public class Order {
    String orderId, orderTotal, orderTime, status;
    ArrayList<Product> itemsOrdered;


    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", orderTotal='" + orderTotal + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", status='" + status + '\'' +
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItemsOrdered(ArrayList<Product> itemsOrdered) {
        this.itemsOrdered = itemsOrdered;
    }
}
