package com.chatapp.application.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chatapp.application.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        continueBtn = findViewById(R.id.continueBtn);
    }

    public void OnPressedContinue(View view) {
        continueBtn.setPressed(true);
        startActivity(new Intent(StartActivity.this, RegisterActivity.class));
        finish();
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }
}