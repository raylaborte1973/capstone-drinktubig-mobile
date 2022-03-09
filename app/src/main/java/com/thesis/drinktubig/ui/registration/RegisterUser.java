package com.thesis.drinktubig.ui.registration;

public class RegisterUser {

    private String Email;
    private String Fullname;
    private String Address;
    private String PhoneNumber;
    private String Usertype;
    private String usertoken;
    private String Status;
    private String CreatedAt;

    public RegisterUser(){

    }

    public RegisterUser(String email, String fullname, String address, String phonenumber, String usertype, String usertoken, String status, String createdAt) {
        this.Email = email;
        this.Fullname = fullname;
        this.Address = address;
        this.PhoneNumber = phonenumber;
        this.Usertype = usertype;
        this.usertoken = usertoken;
        this.Status = status;
        this.CreatedAt = createdAt;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phonenumber) {
        PhoneNumber = phonenumber;
    }

    public String getUsertype() {
        return Usertype;
    }

    public void setUsertype(String usertype) {
        Usertype = usertype;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }
}
