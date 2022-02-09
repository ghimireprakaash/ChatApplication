package com.chatapp.application.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chatapp.application.CheckUserOnlineOfflineState;
import com.chatapp.application.R;
import com.chatapp.application.SharedPref;

public class AboutActivity extends AppCompatActivity {
    TextView appName, aboutApp;
    ImageView aboutImage;

    private SharedPref sharedPreferences;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = new SharedPref(this);
        if (sharedPreferences.loadNightMode()) {
            setTheme(R.style.AppTheme_DarkMode);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        } else {
            setTheme(R.style.AppTheme);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_about);

        appName = findViewById(R.id.appName);
        aboutApp = findViewById(R.id.aboutApp);
        aboutImage = findViewById(R.id.aboutImage);

        String appTitle = "Viber Lite Version";
        appName.setText(appTitle);

        String about = "This app is the clone of Viber,\n"+"and\n"+
                "Is still in development phase.\n"+
                "This app uses simple vector assets.";
        aboutApp.setText(about);

        aboutImage.setImageResource(R.drawable.about_chat_image);
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
}