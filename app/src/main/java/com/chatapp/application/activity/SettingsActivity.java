package com.chatapp.application.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chatapp.application.CheckUserOnlineOfflineState;
import com.chatapp.application.R;
import com.chatapp.application.SharedPref;
import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends AppCompatActivity {
    ImageView buttonBack;
    Toolbar settingsToolbar;
    MaterialCardView accountLayout, appearanceLayout;

    //Child items for accountLayout, and appearanceLayout
    LinearLayout accountChildLayout, appearanceChildLayout;
    TextView deleteAccountTxt, classicThemeTxt, darkThemeTxt;

    private SharedPref sharedPreferences;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = new SharedPref(this);
        if (sharedPreferences.loadNightMode()){
            setTheme(R.style.AppTheme_DarkMode);

            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        } else {
            setTheme(R.style.AppTheme);

            getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_settings);

        //Initialize fields, Views
        init();

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        buttonBack.setOnClickListener(v -> finish());


        //OnClickEvents on Views
        accountLayout.setOnClickListener(v -> {
            if (accountChildLayout.getVisibility() == View.GONE) {
                accountChildLayout.setVisibility(View.VISIBLE);
            } else {
                accountChildLayout.setVisibility(View.GONE);
            }
        });

        appearanceLayout.setOnClickListener(v -> {
            if (appearanceChildLayout.getVisibility() == View.GONE) {
                appearanceChildLayout.setVisibility(View.VISIBLE);
            } else {
                appearanceChildLayout.setVisibility(View.GONE);
            }
        });
    }

    private void init(){
        buttonBack = findViewById(R.id.buttonBack);

        accountLayout = findViewById(R.id.accountLayout);
        accountChildLayout = findViewById(R.id.accountChildLayout);
        deleteAccountTxt = findViewById(R.id.deleteAccountTxt);

        appearanceLayout = findViewById(R.id.appearanceLayout);
        appearanceChildLayout = findViewById(R.id.appearanceChildLayout);
        classicThemeTxt = findViewById(R.id.classicThemeTxt);
        darkThemeTxt = findViewById(R.id.darkThemeTxt);
    }



    @Override
    protected void onStart() {
        super.onStart();

        CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
        userOnlineOfflineState.checkOnlineOrOfflineStatus("Online");
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




    public void deleteAccount(View view) {
        deleteAccountTxt.setPressed(true);
    }

    public void setLightTheme(View view) {
        classicThemeTxt.setPressed(true);

        sharedPreferences.saveNightModeState(false);
        applyTheme();
    }

    public void setDarkTheme(View view) {
        darkThemeTxt.setPressed(true);

        sharedPreferences.saveNightModeState(true);
        applyTheme();
    }

    private void applyTheme() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}