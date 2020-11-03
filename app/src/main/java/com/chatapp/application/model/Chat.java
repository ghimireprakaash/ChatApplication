package com.chatapp.application.model;

public class Chat {
    private String username;
    private String emptyMessageChat;

    private String sender;
    private String message;
    private String receiver;
    private String image;

    public Chat() {
    }


    //When empty message is set
    public Chat(String username, String emptyMessageChat) {
        this.username = username;
        this.emptyMessageChat = emptyMessageChat;
    }

    public String getUsername() {
        return username;
    }

    public String setUsername(String username) {
        this.username = username;
        return username;
    }

    public String getEmptyMessageChat() {
        return emptyMessageChat;
    }

    public void setEmptyMessageChat(String emptyMessageChat) {
        this.emptyMessageChat = emptyMessageChat;
    }




    //When message is sent, and received
    public Chat(String sender, String message, String receiver, String image) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.image = image;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
