package com.chatapp.application.model;

import android.net.Uri;

public class ContactLists {
    public String contact_ID;
    public String image;
    public Uri photoURI;
    public String contact_name;
    public String contact_number;

    public ContactLists(){
    }

    public ContactLists(String contact_ID, String image, Uri photoURI, String contact_name, String contact_number) {
        this.contact_ID = contact_ID;
        this.image = image;
        this.photoURI = photoURI;
        this.contact_name = contact_name;
        this.contact_number = contact_number;
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
