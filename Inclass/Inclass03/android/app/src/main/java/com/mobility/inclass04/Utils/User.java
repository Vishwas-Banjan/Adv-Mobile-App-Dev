package com.mobility.inclass04.Utils;

import java.io.Serializable;

public class User implements Serializable {
    String userFirstName, userLastName, userEmail, userPassword, userCity, userGender;

    public User(String userFirstName, String userLastName, String userEmail, String userPassword, String userCity, String userGender) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userCity = userCity;
        this.userGender = userGender;
    }

    public User() {
    }

    public User(String userFirstName, String userLastName, String userEmail, String userCity, String userGender) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userCity = userCity;
        this.userGender = userGender;
    }

    @Override
    public String toString() {
        return "User{" +
                "userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userCity='" + userCity + '\'' +
                ", userGender='" + userGender + '\'' +
                '}';
    }


    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserCity() {
        return userCity;
    }

    public String getUserGender() {
        return userGender;
    }

}
