package com.chatapp.application.notifications;

public class Data {
    private String user, body, title, sent;
    private Integer icon;

    public Data() {
    }

    public Data(Integer icon, String title, String user, String body, String sent) {
        this.icon = icon;
        this.title = title;
        this.user = user;
        this.body = body;
        this.sent = sent;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }
}
