package com.thesis.drinktubig.beans;

public class StoreBean {

    String StoreImage;
    String StoreName;
    String StoreLocation;
    String StoreOpen;
    String StoreContactNumber;

    public StoreBean(){}

    public StoreBean(String storeImage, String storeName, String storeLocation, String storeOpen, String storeContactNumber) {
        StoreImage = storeImage;
        StoreName = storeName;
        StoreLocation = storeLocation;
        StoreOpen = storeOpen;
        StoreContactNumber = storeContactNumber;
    }

    public String getStoreImage() {
        return StoreImage;
    }

    public void setStoreImage(String storeImage) {
        StoreImage = storeImage;
    }

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public String getStoreLocation() {
        return StoreLocation;
    }

    public void setStoreLocation(String storeLocation) {
        StoreLocation = storeLocation;
    }

    public String getStoreOpen() {
        return StoreOpen;
    }

    public void setStoreOpen(String storeOpen) {
        StoreOpen = storeOpen;
    }

    public String getStoreContactNumber() {
        return StoreContactNumber;
    }

    public void setStoreContactNumber(String storeContactNumber) {
        StoreContactNumber = storeContactNumber;
    }
}
