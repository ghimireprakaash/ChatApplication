package com.chatapp.application.model;

public class ContactLists {
//    private String contact_ID;
    private int contact_user_image;
    private String contact_name;
    private String contact_number;

    public ContactLists(){
    }

    public ContactLists(int contact_user_image, String contact_name, String contact_number){
        this.contact_user_image = contact_user_image;
        this.contact_name = contact_name;
        this.contact_number = contact_number;
    }

    public int getContact_user_image(){return contact_user_image;}

    public void setContact_user_image(int contact_user_image){this.contact_user_image = contact_user_image;}

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
