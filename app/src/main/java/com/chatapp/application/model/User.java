package com.chatapp.application.model;

public class User {
    private String uid;
    private String username;
    private String image;
    private String contact;
    private String fullcontactnumber;
    private String status;

    public User() {
    }

    public User(String uid, String username, String image, String contact, String fullcontactnumber, String status) {
        this.uid = uid;
        this.username = username;
        this.image = image;
        this.contact = contact;
        this.fullcontactnumber = fullcontactnumber;
        this.status = status;
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

    public String getFullcontactnumber() {
        return fullcontactnumber;
    }

    public void setFullcontactnumber(String fullcontactnumber) {
        this.fullcontactnumber = fullcontactnumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
