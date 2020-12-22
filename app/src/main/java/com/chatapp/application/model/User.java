package com.chatapp.application.model;

public class User {
    private String uid;
    private String username;
    private String image;
    private String contact;
    private String state;
    private String time;
    private String date;

    public User() {
    }

    public User(String uid, String username, String image, String contact, String state, String time, String date) {
        this.uid = uid;
        this.username = username;
        this.image = image;
        this.contact = contact;
        this.state = state;
        this.time = time;
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
