package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.chatapp.application.R;
import com.chatapp.application.fragments.ChatFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ProfileSetupActivity extends AppCompatActivity {
    private static final int GalleryPick = 1;

    private ImageView setupProfileImage;
    private EditText setupProfileName, setupProfileDOB;
    private Button btnSet;


    private String currentUserID;


    //Declaring Firebase instances
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //Firebase storage
    private StorageReference userProfileImagesStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        //Initializing Instance
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        userProfileImagesStorage = FirebaseStorage.getInstance().getReference().child("Profile Images");




        init();




        retrieveUserInfo();




        //Image setup via gallery access
        setupProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileSetupIntent = new Intent();
                profileSetupIntent.setAction(Intent.ACTION_GET_CONTENT);
                profileSetupIntent.setType("image/*");
                startActivityForResult(profileSetupIntent, GalleryPick);
            }
        });



        //Date Picker
        setupProfileDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileSetupActivity.this,
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





        //On click button set, it setups the user information
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileSetup();
            }
        });
    }




    // Initializing fields
    private void init() {
        setupProfileImage = findViewById(R.id.setupProfileImage);
        setupProfileName = findViewById(R.id.setupProfileName);
        setupProfileDOB = findViewById(R.id.setupProfileDOB);

        btnSet = findViewById(R.id.buttonSet);
    }





    private void retrieveUserInfo() {
        databaseReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userProfileImage")) && (snapshot.hasChild("username"))){

                    String getUserProfileImage = snapshot.child("userProfileImage").getValue().toString();
                    String getUserName = snapshot.child("username").getValue().toString();
                    String getUserDOB = snapshot.child("dob").getValue().toString();


                    Picasso.get().load(getUserProfileImage).into(setupProfileImage);
                    setupProfileName.setText(getUserName);
                    setupProfileDOB.setText(getUserDOB);


                } else if ((snapshot.exists()) && (snapshot.hasChild("username"))) {

                    String getUserName = snapshot.child("username").getValue().toString();
                    String getUserDOB = snapshot.child("dob").getValue().toString();

                    setupProfileName.setText(getUserName);
                    setupProfileDOB.setText(getUserDOB);

                } else {
                    //Do Nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                assert result != null;
                Uri resultUri = result.getUri();


                StorageReference filePath = userProfileImagesStorage.child(currentUserID + ".jpg");
                filePath.putFile(resultUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()){
                            String exception = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(getApplicationContext(), "Error selecting image "+ exception, Toast.LENGTH_LONG).show();
                        } else {
                            //Uploads the profile to firebase storage
                            final String downloadUrl = Objects.requireNonNull(task.getResult()).getStorage().getDownloadUrl().toString();

                            databaseReference.child("Users").child(currentUserID).child("userProfileImage")
                                    .setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                //Do nothing if successful
                                            }else {
                                                //returns error message if uploading image to the firebase database is failed
                                                Toast.makeText(getApplicationContext(), "Error: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        }
    }




    //User Profile Setup
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

            String getCurrentUserPhoneNumber = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();

            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("username", profileName);
            profileMap.put("dob", profileDOB);
            profileMap.put("contact", getCurrentUserPhoneNumber);

            databaseReference.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Profile Setup Successful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
}