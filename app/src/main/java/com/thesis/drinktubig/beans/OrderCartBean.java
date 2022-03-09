package com.thesis.drinktubig.beans;

import java.util.ArrayList;

public class OrderCartBean {

    private static ArrayList<OrderCartBean> orders = new ArrayList<>();

    private String productID;
    private String productName;
    private String productCategory;
    private String productPrice;
    private String productQuantity;

    public OrderCartBean(){}

    public OrderCartBean(String productID, String productName, String productCategory, String productPrice, String productQuantity) {
        this.productID = productID;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    public static void clearOrders() {
        orders = new ArrayList<>();
    }

    public void setOrders(OrderCartBean order) {
        orders.add(order);
    }

    public static ArrayList<OrderCartBean> getOrders() {
        return orders;
    }

    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }
}
