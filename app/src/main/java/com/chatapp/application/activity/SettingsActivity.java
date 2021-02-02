package com.chatapp.application.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chatapp.application.CheckUserOnlineOfflineState;
import com.chatapp.application.R;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    Toolbar settingsToolbar;
    TextView accountLayout, appearanceLayout;

    //Child items for accountLayout, and appearanceLayout
    LinearLayout accountChildLayout, appearanceChildLayout;
    TextView deleteAccountTxt, classicThemeTxt, darkThemeTxt;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_settings);

        //Initialize fields, Views
        init();

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        settingsToolbar.setNavigationOnClickListener(v -> finish());


        //OnClickEvents on Views
        accountLayout.setOnClickListener(v -> {
            accountLayout.setPressed(true);
            if (accountChildLayout.getVisibility() == View.GONE) {
                accountChildLayout.setVisibility(View.VISIBLE);
            } else {
                accountChildLayout.setVisibility(View.GONE);
            }
        });

        appearanceLayout.setOnClickListener(v -> {
            appearanceLayout.setPressed(true);
            if (appearanceChildLayout.getVisibility() == View.GONE) {
                appearanceChildLayout.setVisibility(View.VISIBLE);
            } else {
                appearanceChildLayout.setVisibility(View.GONE);
            }
        });
    }

    private void init(){
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
    }

    public void setDarkTheme(View view) {
        darkThemeTxt.setPressed(true);
    }
}