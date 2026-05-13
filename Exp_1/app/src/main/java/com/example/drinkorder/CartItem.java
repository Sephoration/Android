package com.example.drinkorder;

/** 购物车条目实体类 — 对应Cart表（含JOIN关联的商品名和图片） */
public class CartItem {
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private double price;
    private String productName;
    private int imageResId;

    public CartItem() {
    }

    public CartItem(int id, int userId, int productId, int quantity, double price, String productName, int imageResId) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
        this.imageResId = imageResId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
}
