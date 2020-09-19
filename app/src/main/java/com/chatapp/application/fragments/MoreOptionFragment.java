package com.chatapp.application.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.chatapp.application.profile.ProfileUpdate;
import com.chatapp.application.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MoreOptionFragment extends Fragment {
    private TextView editOption;

    private ImageView userProfileImage;
    private TextView userProfileName, userProfileContact, userDOB;


    //Declaring Firebase Instance
    private FirebaseAuth firebaseAuth;
    private String getCurrentUserID;
    private DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_more_option, container, false);

        //Initializing firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        //Getting Current user id
        getCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();




        //Initializing fields
        editOption = view.findViewById(R.id.editOption);

        userProfileImage = view.findViewById(R.id.profileImage);
        userProfileName = view.findViewById(R.id.profileName);
        userProfileContact = view.findViewById(R.id.profileContactNumber);
        userDOB = view.findViewById(R.id.profileDOB);




        retrieveUserInfo();





        editOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProfileUpdate.class));
            }
        });

        return view;
    }




    //Retrieving Current User Information
    private void retrieveUserInfo() {
        databaseReference.child("Users").child(getCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userimage")) && (snapshot.hasChild("username"))){

                    String getUserProfileImage = snapshot.child("userimage").getValue().toString();
                    String getUserName = snapshot.child("username").getValue().toString();
                    String getUserContactNumber = snapshot.child("contact").getValue().toString();
                    String getUserDOB = snapshot.child("dob").getValue().toString();

                    userProfileName.setText(getUserName);
                    userProfileContact.setText(getUserContactNumber);
                    userDOB.setText(getUserDOB);

                } else if ((snapshot.exists() && (snapshot.hasChild("username")))){

                    String getUserName = snapshot.child("username").getValue().toString();
                    String getUserContactNumber = snapshot.child("contact").getValue().toString();
                    String getUserDOB = snapshot.child("dob").getValue().toString();

                    userProfileName.setText(getUserName);
                    userProfileContact.setText(getUserContactNumber);
                    userDOB.setText(getUserDOB);

                } else {
                    //DO Nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}