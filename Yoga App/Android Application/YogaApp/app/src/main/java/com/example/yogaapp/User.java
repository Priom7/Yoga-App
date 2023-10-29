package com.example.yogaapp;

public class User {
    private String userId;
    private String userName;

    public User() {
        // Required empty public constructor
    }

    public User(String s, String s1) {
        this.userId = s;
        this.userName = s1;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}