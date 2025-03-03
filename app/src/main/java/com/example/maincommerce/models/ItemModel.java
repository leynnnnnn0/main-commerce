package com.example.maincommerce.models;

public class ItemModel {
    public String name;
    public String description;
    public String image;
    public double price;
    public int stock;
    public int sold;

    public ItemModel() {
    }

    public ItemModel(String name, String description, String image, double price, int stock) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.stock = stock;
        this.sold = 0;
    }

    public ItemModel(String name, String description, String image, double price, int stock, int sold) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.stock = stock;
        this.sold = sold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
