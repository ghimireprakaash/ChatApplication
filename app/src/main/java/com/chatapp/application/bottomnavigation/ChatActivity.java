package com.chatapp.application.bottomnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.chatapp.application.R;
import com.chatapp.application.activity.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;


    //Declaring instances
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        //Initializing instances
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(itemSelectedListeners);

        //set Default fragment
        if (savedInstanceState == null){
            bottomNavigationView.setSelectedItemId(R.id.chat);
        }



        //verifies if current user is null or not null
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null){
            // Do nothing

        } else {
            verifyUserExistence();
        }
    }




    //To verify user existence
    private void verifyUserExistence() {
        String checkCurrentUserID;
        checkCurrentUserID = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child("Users").child(checkCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("username").exists()){
                    // If username exists then if statement executes

                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
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
            switch (item.getItemId()){
                case R.id.chat:
                    return true;

                case R.id.contact:
                    startActivity(new Intent(getApplicationContext(), ContactListsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.moreOption:
                    startActivity(new Intent(getApplicationContext(), MoreActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }

            return false;
        }
    };
}