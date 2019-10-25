package com.example.kadir.agricultureprojectsupportside.ShortCut;


import com.example.kadir.agricultureprojectsupportside.datatypes.farmdata.Farm;

public class User {
    private String userName;
    private String userLastname;
    private String userEmail;
    private String userAddress;
    private String userPhone;
    private Farm[] farms;
    private String userId;


    public User(){

    }

    public User(String userName, String userLastname, String userEmail, String userAddress, String userPhone){
        this.userName = userName;
        this.userLastname = userLastname;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.userPhone = userPhone;
    }

    public Farm[] getFarms() {
        return farms;
    }

    public String getUserLastname() {
        return userLastname;
    }

    public void setUserName(String UserName) {
        this.userName = UserName;
    }

    public void setUserLastname(String UserLastname) {
        this.userLastname = UserLastname;
    }

    public void setUserEmail(String UserEmail) {
        this.userEmail = UserEmail;
    }

    public void setUserAddress(String UserAddress) {
        this.userAddress = UserAddress;
    }

    public void setUserPhone(String UserPhone) {
        this.userPhone = UserPhone;
    }

    public void setFarms(Farm[] farms) {
        this.farms = farms;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;

    }

    public String getUserName(){
        return this.userName;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String UserId) {
        this.userId = UserId;
    }


}
