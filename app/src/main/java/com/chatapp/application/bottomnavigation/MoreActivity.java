package com.chatapp.application.bottomnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.chatapp.application.profile.ProfileUpdate;
import com.chatapp.application.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MoreActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    private TextView editOption;

    private ImageView userProfileImage;
    private TextView userProfileName, userProfileContact, userDOB;


    //Declaring Firebase Instance
    private FirebaseAuth firebaseAuth;
    private String getCurrentUserID;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        //Initializing firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        //Getting Current user id
        getCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();



        //Initializing fields
        init();


        retrieveUserInfo();


        editOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileUpdate.class));
            }
        });



        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(itemSelectedListeners);

        //set Default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.moreOption);
        }
    }



    //Initializing ImageView and TextViews
    private void init() {
        editOption = findViewById(R.id.editOption);

        userProfileImage = findViewById(R.id.profileImage);
        userProfileName = findViewById(R.id.profileName);
        userProfileContact = findViewById(R.id.profileContactNumber);
        userDOB = findViewById(R.id.profileDOB);
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




    private BottomNavigationView.OnNavigationItemSelectedListener
            itemSelectedListeners = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.chat:
                    startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.contact:
                    startActivity(new Intent(getApplicationContext(), ContactListsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.moreOption:
                    return true;
            }
            return false;
        }
    };
}