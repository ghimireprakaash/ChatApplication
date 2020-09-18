package com.chatapp.application.bottomnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chatapp.application.profile.ProfileUpdate;
import com.chatapp.application.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MoreActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    TextView editOption, contactNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        editOption = findViewById(R.id.editOption);
        contactNumber = findViewById(R.id.contactNumber);

//        String getContactNumber = getIntent().getStringExtra("getFullPhoneNumber");
        contactNumber.setText(getIntent().getStringExtra("getFullPhoneNumber"));

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