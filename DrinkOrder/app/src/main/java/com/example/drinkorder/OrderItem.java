package com.example.drinkorder;

/** 订单实体类 — 对应Orders表 */
public class OrderItem {
    private int id;
    private int userId;
    private String orderTime;
    private double totalPrice;
    private String items;

    public OrderItem() {
    }

    public OrderItem(int id, int userId, String orderTime, double totalPrice, String items) {
        this.id = id;
        this.userId = userId;
        this.orderTime = orderTime;
        this.totalPrice = totalPrice;
        this.items = items;
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

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }
}
