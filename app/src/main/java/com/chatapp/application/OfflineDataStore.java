package com.chatapp.application;

import android.app.Application;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OfflineDataStore extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);

        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
    }
}
