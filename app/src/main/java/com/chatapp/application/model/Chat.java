package com.chatapp.application.model;

public class Chat {
    public Chat() {
    }


    private String id;

    //Gets ids of user with whom chat is going on
    public Chat(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




//--------------------------------------------------------------------------------------------------------------------------------------//




    //gets history of chats like sender id, receiver id, messages, and also messageSentTime
    private String message;
    private String sender;
    private String receiver;
    private String messageSentTime;


    public Chat(String message, String sender, String receiver, String messageSentTime) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.messageSentTime = messageSentTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageSentTime() {
        return messageSentTime;
    }

    public void setMessageSentTime(String messageSentTime) {
        this.messageSentTime = messageSentTime;
    }
}
