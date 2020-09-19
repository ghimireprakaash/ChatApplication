package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import com.chatapp.application.R;
import com.chatapp.application.fragments.ChatFragment;
import com.chatapp.application.fragments.ContactListsFragment;
import com.chatapp.application.fragments.MoreOptionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    private BottomNavigationView.OnNavigationItemSelectedListener
            itemSelectedListeners = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.chat:
                    selectedFragment = new ChatFragment();
                    break;

                case R.id.contact:
                    selectedFragment = new ContactListsFragment();
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
}