package com.chatapp.application.model;

public class Contacts {
    private String contact_name;
    private String contact_number;
    private String userName_firstLetter_and_lastLetter;

    private String Uid;

    public Contacts(){
    }

    public Contacts(String contact_name, String contact_number, String userName_firstLetter_and_lastLetter, String Uid) {
        this.contact_name = contact_name;
        this.contact_number = contact_number;
        this.userName_firstLetter_and_lastLetter = userName_firstLetter_and_lastLetter;
        this.Uid = Uid;
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

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}