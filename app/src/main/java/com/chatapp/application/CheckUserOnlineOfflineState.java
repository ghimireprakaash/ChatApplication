package com.chatapp.application;

import android.annotation.SuppressLint;
import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class CheckUserOnlineOfflineState {
    Context context;
    DatabaseReference databaseReference;

    public CheckUserOnlineOfflineState(Context context) {
        this.context = context;
    }

    @SuppressLint("SimpleDateFormat")
    public void checkOnlineOrOfflineStatus(String state){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());


        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd");
        saveCurrentDate = currentDate.format(calendar.getTime());

        HashMap<String, Object> userStateMap = new HashMap<>();
        userStateMap.put("time", saveCurrentTime);
        userStateMap.put("date", saveCurrentDate);
        userStateMap.put("state", state);

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(currentUser).child("userState").updateChildren(userStateMap);
    }
}
