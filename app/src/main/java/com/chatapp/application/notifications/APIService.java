package com.chatapp.application.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type: application/json",
            "Authorization:key=AAAAraL7tgE:APA91bGZsKTufG7bDZPehDzXhSi5bERF2li2d5bWWJTNgAAfscv5hmDkiBoFzvMDoopn4gIouPWx5-C1uI0P5S3SIj4WoOttZOOFlFzipje9HdV7oxgH8vKtnyrYQAFnSnpSzPdhhR8X"
    })

    @POST("/fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
