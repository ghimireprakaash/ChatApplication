package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.chatapp.application.R;
import com.chatapp.application.bottomnavigation.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ImageView setupProfileImage;
    private EditText setupProfileName, setupProfileDOB;


    private Button btnSet;


    private String currentUserID;

    //Declaring Firebase instances
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Instance
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUserID = firebaseAuth.getCurrentUser().getUid();

        init();



        //Date Picker
        setupProfileDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month = month+1;
                                String date = day+"/"+month+"/"+year;
                                setupProfileDOB.setText(date);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });




//        verifyUserExistence();


        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileSetup();
            }
        });
    }

    private void profileSetup() {
        String profileName = setupProfileName.getText().toString();
        String profileDOB = setupProfileDOB.getText().toString();

        if (TextUtils.isEmpty(profileName)){
            setupProfileName.setError("Name required");
            setupProfileName.requestFocus();

        } else if (TextUtils.isEmpty(profileDOB)){
            setupProfileDOB.setError("Date of birth required");
            setupProfileDOB.requestFocus();

        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("username", profileName);
            profileMap.put("dob", profileDOB);

            databaseReference.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Profile Setup Successful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    private void init() {
        setupProfileImage = findViewById(R.id.setupProfileImage);
        setupProfileName = findViewById(R.id.setupProfileName);
        setupProfileDOB = findViewById(R.id.setupProfileDOB);

        btnSet = findViewById(R.id.buttonSet);
    }





    //Verifies if the user is existed
//    private void verifyUserExistence() {
//        String checkCurrentUserID;
//        checkCurrentUserID = firebaseAuth.getCurrentUser().getUid();
//
//        databaseReference.child(checkCurrentUserID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if ((snapshot.child("username").exists())){
//                 //If user exists this condition is applied
//                } else {
//                    init();
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}