package com.chatapp.application.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class Contacts {
    private String contact_ID;
    private String image;
    private Bitmap photo;
    private Uri photoURI;
    private String contact_name;
    private String contact_number;
    private String userName_firstLetter_and_lastLetter;

    public Contacts(){
    }

    public Contacts(String contact_ID, String image, Bitmap photo, Uri photoURI, String contact_name, String contact_number, String userName_firstLetter_and_lastLetter) {
        this.contact_ID = contact_ID;
        this.image = image;
        this.photo = photo;
        this.photoURI = photoURI;
        this.contact_name = contact_name;
        this.contact_number = contact_number;
        this.userName_firstLetter_and_lastLetter = userName_firstLetter_and_lastLetter;
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

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getUserName_firstLetter_and_lastLetter() {
        return userName_firstLetter_and_lastLetter;
    }

    public void setUserName_firstLetter_and_lastLetter(String userName_firstLetter_and_lastLetter) {
        this.userName_firstLetter_and_lastLetter = userName_firstLetter_and_lastLetter;
    }
}