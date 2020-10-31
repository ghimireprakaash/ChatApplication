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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class MoreOptionFragment extends Fragment {
    private TextView editOption;

    private ImageView userProfileImage;
    private TextView camera_icon;

    private TextView userProfileName, userProfileContact, userDOB;


    //Declaring Firebase Instance
    private FirebaseAuth firebaseAuth;
    private String currentUserID;
    private DatabaseReference databaseReference;
    private StorageReference userProfileStorageRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_more_option, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initializing firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Getting Current user id
        currentUserID = firebaseAuth.getCurrentUser().getUid();

        userProfileStorageRef = FirebaseStorage.getInstance().getReference().child(currentUserID).child("Profile Images");



        //Initializing fields
        editOption = view.findViewById(R.id.editOption);

        userProfileImage = view.findViewById(R.id.profileImage);
        camera_icon = view.findViewById(R.id.camera_icon);

        userProfileName = view.findViewById(R.id.profileName);
        userProfileContact = view.findViewById(R.id.profileContactNumber);
        userDOB = view.findViewById(R.id.profileDOB);


        editOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(getContext(), ProfileUpdate.class);
                startActivity(editIntent);
            }
        });


        userProfileImage.isPressed();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getActivity(), ProfileUpdate.class);
                startActivity(startIntent);
            }
        });
    }



    //Retrieving Current User Information
    private void retrieveUserInfo() {
        databaseReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image")) && (snapshot.hasChild("username"))){

                    String getUserProfileImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                    String getUserName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    String getUserContactNumber = Objects.requireNonNull(snapshot.child("contact").getValue()).toString();
                    String getUserDOB = Objects.requireNonNull(snapshot.child("dob").getValue()).toString();


                    Picasso.get().load(getUserProfileImage).into(userProfileImage);
                    userProfileName.setText(getUserName);
                    userProfileContact.setText(getUserContactNumber);
                    userDOB.setText(getUserDOB);

                } else if ((snapshot.exists() && (snapshot.hasChild("username")))){

                    String getUserName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    String getUserContactNumber = Objects.requireNonNull(snapshot.child("contact").getValue()).toString();
                    String getUserDOB = Objects.requireNonNull(snapshot.child("dob").getValue()).toString();

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



    @Override
    public void onStart() {
        super.onStart();

        //retrieves user info on start of the fragment
        retrieveUserInfo();
    }
}