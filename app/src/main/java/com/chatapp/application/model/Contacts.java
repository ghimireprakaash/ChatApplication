package com.chatapp.application.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class Contacts {
    public String contact_ID;
    public String image;
    public Bitmap photo;
    public Uri photoURI;
    public String contact_name;

    public Contacts(){
    }

    public Contacts(String contact_ID, String image, Bitmap photo, Uri photoURI, String contact_name) {
        this.contact_ID = contact_ID;
        this.image = image;
        this.photo = photo;
        this.photoURI = photoURI;
        this.contact_name = contact_name;
    }

    public String getContact_ID() {
        return contact_ID;
    }

    public void setContact_ID(String contact_ID) {
        this.contact_ID = contact_ID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public Uri getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(Uri photoURI) {
        this.photoURI = photoURI;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }



    // Getter Setter for retrieving from firebase
    private String uid;
    private String username;

    public Contacts(String uid, String image, String username) {
        this.uid = uid;
        this.image = image;
        this.username = username;
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
}
