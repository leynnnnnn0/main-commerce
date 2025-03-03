package com.example.maincommerce.models;

public class CartItemModel {
    private String itemName;
    private String itemDescription;
    private double itemPrice;
    private Long quantity;

    private String itemImage;

    public String getItemImage() {
        return itemImage;
    }

    public CartItemModel(){}

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public Long getQuantity() {
        return quantity;
    }
}
