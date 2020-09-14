package com.chatapp.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.chatapp.application.BottomNavigation.ChatFragment;
import com.chatapp.application.BottomNavigation.ContactListsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(itemSelectedListeners);

        //set Default fragment
        if (savedInstanceState == null){
            bottomNavigationView.setSelectedItemId(R.id.chat);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            itemSelectedListeners = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.chat:
                    selectedFragment = new ChatFragment();
                    break;

                case R.id.groupChat:
                    selectedFragment = new ContactListsFragment();
                    break;
            }

            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

            return true;
        }
    };
}