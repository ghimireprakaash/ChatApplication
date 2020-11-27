package com.chatapp.application.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chatapp.application.R;

public class VisitContactProfile extends AppCompatActivity {
    private TextView buttonBack, contactName, contactPhoneNumber;
    private ImageView contactProfileImage;

    String contact_name, contact_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_contact_profile);

        buttonBack = findViewById(R.id.contactButtonBack);
        contactName = findViewById(R.id.contactName);
        contactPhoneNumber = findViewById(R.id.contactPhoneNumber);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contact_name = getIntent().getStringExtra("contact_username");
        contactName.setText(contact_name);

        contact_number = getIntent().getStringExtra("contact_number");
        contactPhoneNumber.setText(contact_number);
        contactPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactPhoneNumber.setPressed(true);
            }
        });
    }
}