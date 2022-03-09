package com.thesis.drinktubig.beans;

public class StoreProductsBean {

    private String Product_Image;
    private String Product_Name;
    private String Product_Category;
    private String Product_Price;

    public StoreProductsBean(){}

    public StoreProductsBean(String product_Image, String product_Name, String product_Category, String product_Price){
        this.Product_Image = product_Image;
        this.Product_Name = product_Name;
        this.Product_Category = product_Category;
        this.Product_Price = product_Price;
    }

    public String getProduct_Image() {
        return Product_Image;
    }

    public String getProduct_Name() {
        return Product_Name;
    }

    public String getProduct_Category() {
        return Product_Category;
    }

    public String getProduct_Price() {
        return Product_Price;
    }

}
