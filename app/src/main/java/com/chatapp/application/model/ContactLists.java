package com.chatapp.application.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class ContactLists {
    public String contact_ID;
    public Bitmap photo;
    public Uri photoURI;
    public String contact_name;
    public String contact_number;

    public ContactLists(){
    }


    public String getContact_ID() {
        return contact_ID;
    }

    public void setContact_ID(String contact_ID) {
        this.contact_ID = contact_ID;
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
}
