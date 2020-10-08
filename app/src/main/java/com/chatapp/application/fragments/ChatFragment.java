package com.chatapp.application.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.chatapp.application.R;
import com.chatapp.application.profile.ProfileSetupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class ChatFragment extends Fragment {
    //Declaring instances
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //Declaring string variable currentUserID
    private String currentUserID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //Initializing instances
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();


        //verifies if current user is null or not null
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null){
            // Do nothing

        } else {
            verifyUserExistence();
        }


        return view;
    }




    //To verify user existence
    private void verifyUserExistence() {
        databaseReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("username").exists())){
                    // If username doesn't exists then moves to profile setup
                    try {
                        Intent intent = new Intent(getContext(), ProfileSetupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}