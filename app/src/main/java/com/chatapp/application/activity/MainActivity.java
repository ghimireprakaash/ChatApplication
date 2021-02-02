package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import com.chatapp.application.CheckUserOnlineOfflineState;
import com.chatapp.application.R;
import com.chatapp.application.fragments.ChatFragment;
import com.chatapp.application.fragments.ContactsFragment;
import com.chatapp.application.fragments.MoreOptionFragment;
import com.chatapp.application.notifications.Token;
import com.chatapp.application.profile.ProfileSetupActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    BottomNavigationView bottomNavigationView;

    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

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


        //updating token
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String token){
        Token token1 = new Token(token);
        databaseReference.child("Tokens").child(currentUser.getUid()).setValue(token1);
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener
            itemSelectedListeners = item -> {

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
                assert selectedFragment != null;
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

                return true;
            };


    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null){
            startActivity(new Intent(getApplicationContext(), ProfileSetupActivity.class));
        } else {
            verifyUserExistence();

            CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
            userOnlineOfflineState.checkOnlineOrOfflineStatus("Online");

            SharedPreferences sharedPreferences = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Current_USERID", currentUser.getUid());
            editor.apply();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
        userOnlineOfflineState.checkOnlineOrOfflineStatus("Offline");
    }

    @Override
    protected void onResume() {
        super.onResume();

        CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
        userOnlineOfflineState.checkOnlineOrOfflineStatus("Online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
        userOnlineOfflineState.checkOnlineOrOfflineStatus("Offline");
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
}