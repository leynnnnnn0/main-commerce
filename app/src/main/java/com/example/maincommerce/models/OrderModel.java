package com.example.maincommerce.models;

import java.util.ArrayList;

public class OrderModel {
    private String orderId;
    private String orderDate;
    private String orderStatus;
    private ArrayList<CartItemModel> items;

    public String getOrderId() {
        return orderId;
    }

    public ArrayList<CartItemModel> getItems() {
        return items;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
