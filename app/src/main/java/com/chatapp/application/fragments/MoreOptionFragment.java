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
import com.chatapp.application.activity.AboutActivity;
import com.chatapp.application.activity.SettingsActivity;
import com.chatapp.application.profile.ProfileUpdate;
import com.chatapp.application.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class MoreOptionFragment extends Fragment {
    private ImageView editOption, userProfileImage;
    private TextView camera_icon;

    private TextView userProfileName, userProfileContact, userDOB;
    private MaterialCardView settingsCard, aboutCard;


    //Declaring Firebase Instance
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();

        return inflater.inflate(R.layout.fragment_more_option, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initializing firebase instance
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Getting Current user id
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        //Initializing fields
        editOption = view.findViewById(R.id.editOption);

        userProfileImage = view.findViewById(R.id.profileImage);
        camera_icon = view.findViewById(R.id.camera_icon);
        camera_icon.setVisibility(View.VISIBLE);

        userProfileName = view.findViewById(R.id.profileName);
        userProfileContact = view.findViewById(R.id.profileContactNumber);
        userDOB = view.findViewById(R.id.profileDOB);

        settingsCard = view.findViewById(R.id.settingsCard);
        aboutCard = view.findViewById(R.id.aboutCard);

        editOption.setOnClickListener(view1 -> {
            Intent editIntent = new Intent(getContext(), ProfileUpdate.class);
            startActivity(editIntent);
        });


        userProfileImage.isPressed();

        userProfileImage.setOnClickListener(v -> {
            Intent startIntent = new Intent(getActivity(), ProfileUpdate.class);
            startActivity(startIntent);
        });

        settingsCard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        aboutCard.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutActivity.class)));
    }



    //Retrieving Current User Information
    private void retrieveUserInfo() {
        databaseReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image")) && (snapshot.hasChild("username"))){

                    String getUserProfileImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                    String getUserName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    String getUserContactNumber = Objects.requireNonNull(snapshot.child("contact").getValue()).toString();
                    String getUserDOB = Objects.requireNonNull(snapshot.child("dob").getValue()).toString();

                    camera_icon.setVisibility(View.GONE);
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