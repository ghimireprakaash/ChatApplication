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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContactListsActivity extends AppCompatActivity {
    Toolbar toolbar;

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_lists);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(itemSelectedListeners);

        //set Default fragment
        if (savedInstanceState == null){
            bottomNavigationView.setSelectedItemId(R.id.contact);
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener
            itemSelectedListeners = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.chat:
                    startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.contact:
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