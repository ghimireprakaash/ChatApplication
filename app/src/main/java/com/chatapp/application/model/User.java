package com.chatapp.application.model;

public class User {
    private String uid;
    private String username;
    private String image;
    private String contact;

    public User() {
    }

    public User(String uid, String username, String image, String contact) {
        this.uid = uid;
        this.username = username;
        this.image = image;
        this.contact = contact;
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
}
