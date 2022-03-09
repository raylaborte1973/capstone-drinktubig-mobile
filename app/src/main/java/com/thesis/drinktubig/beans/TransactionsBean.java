package com.thesis.drinktubig.beans;

public class TransactionsBean {

    private String StoreName;
    private String ProductName;
    private String ProductPrice;
    private String TotalAmount;
    private String TransactionDate;
    private String CustomerName;

    public TransactionsBean() {}

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String storeName) {
        this.StoreName = storeName;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        this.ProductName = productName;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        this.ProductPrice = productPrice;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.TotalAmount = totalAmount;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String CustomerName) {
        this.CustomerName = CustomerName;
    }
}
