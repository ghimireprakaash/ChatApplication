package com.chatapp.application.notifications;

public class Token {
    /** An FCM token or much commonly known as RegistrationToken.
     * An Id issued by the GCM connection servers to the client app that allows it to receive messages
     */
    private String token;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
