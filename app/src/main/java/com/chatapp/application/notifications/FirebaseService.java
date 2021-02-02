package com.chatapp.application.notifications;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseService";
    FirebaseUser currentUser;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onTokenRefresh: "+refreshedToken);

        if (currentUser != null){
            DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("Tokens");
            Token token = new Token(refreshedToken);

            tokenRef.child(currentUser.getUid()).setValue(token);
        }
    }
}
