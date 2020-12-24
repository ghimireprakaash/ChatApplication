package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.chatapp.application.R;
import com.chatapp.application.fragments.ChatFragment;
import com.chatapp.application.fragments.ContactsFragment;
import com.chatapp.application.fragments.MoreOptionFragment;
import com.chatapp.application.profile.ProfileSetupActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    BottomNavigationView bottomNavigationView;

    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar();

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(itemSelectedListeners);

        //set Default fragment
        if (savedInstanceState == null){
            bottomNavigationView.setSelectedItemId(R.id.chat);
        }
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener
            itemSelectedListeners = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.chat:
                    selectedFragment = new ChatFragment();
                    break;

                case R.id.contact:
                    selectedFragment = new ContactsFragment();
                    break;

                case R.id.moreOption:
                    selectedFragment = new MoreOptionFragment();
                    break;
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

            return true;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null){
            startActivity(new Intent(getApplicationContext(), ProfileSetupActivity.class));
        } else {
            verifyUserExistence();

            checkOnlineOrOfflineStatus("Online");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (currentUser != null){
            checkOnlineOrOfflineStatus("Offline");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentUser != null){
            checkOnlineOrOfflineStatus("Online");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (currentUser != null){
            checkOnlineOrOfflineStatus("Offline");
        }
    }


    //To verify user existence
    private void verifyUserExistence() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("username").exists())){
                    // If username doesn't exists then moves to profile setup
                    try {
                        Intent intent = new Intent(getApplicationContext(), ProfileSetupActivity.class);
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


    @SuppressLint("SimpleDateFormat")
    private void checkOnlineOrOfflineStatus(String state){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentTime;
        if (android.text.format.DateFormat.is24HourFormat(this)){
            currentTime = new SimpleDateFormat("HH:mm");
        }else {
            currentTime = new SimpleDateFormat("hh:mm a");
        }
        saveCurrentTime = currentTime.format(calendar.getTime());


        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd");
        saveCurrentDate = currentDate.format(calendar.getTime());

        HashMap<String, Object> userStateMap = new HashMap<>();
        userStateMap.put("time", saveCurrentTime);
        userStateMap.put("date", saveCurrentDate);
        userStateMap.put("state", state);

        databaseReference.child("Users").child(currentUser.getUid()).child("userState").updateChildren(userStateMap);
    }
}