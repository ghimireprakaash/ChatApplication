package com.chatapp.application.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.chatapp.application.R;
import com.chatapp.application.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ProfileSetupActivity extends AppCompatActivity {
    private final String TAG = "ProfileSetupActivity";
    private static final int REQUEST_CODE = 1;

    private static final int GALLERY_PICK = 1;
    Uri uri;

    //initially setting year, month, and day to 0
    int year = 0;
    int month = 0;
    int day = 0;


    private ImageView setupProfileImage;
    private TextView camera_icon;

    private EditText setupProfileName, setupProfileDOB;
    private Button btnSet;


    //Declaring Firebase instances
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference userProfileImagesStorageRef;
    private StorageReference filePath;

    //declaring current user id variable
    private String currentUserID;

    //Retrieve Image uri if available
    private String imageUri;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_profile_setup);

        //Initializing Instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userProfileImagesStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();



        init();



        setupProfileImage.setPressed(false);
        //Image setup via gallery access
        setupProfileImage.setOnClickListener(view ->
                CropImage.startPickImageActivity(ProfileSetupActivity.this));

        //open calender so user can pick his/her birth date...
        OnDatePicker();

        //On click button set, it setups the user information
        btnSet.setOnClickListener(view -> profileSetup());
    }


    // Initializing fields
    private void init() {
        setupProfileImage = findViewById(R.id.setupProfileImage);
        camera_icon = findViewById(R.id.camera_icon);

        setupProfileName = findViewById(R.id.setupProfileName);
        setupProfileDOB = findViewById(R.id.setupProfileDOB);

        btnSet = findViewById(R.id.buttonSet);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)){
                uri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICK);
            } else {
                startImageCrop(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                assert result != null;
                Uri resultUri = result.getUri();

                //setting icon visibility to gone after successful photo upload
                camera_icon.setVisibility(View.GONE);

                //set image to the view
                setupProfileImage.setImageURI(resultUri);


                //Uploads the profile to firebase storage
                filePath = userProfileImagesStorageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()){
                        String exception = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(getApplicationContext(), "Error selecting image "+ exception, Toast.LENGTH_LONG).show();
                    } else {
                        filePath.getDownloadUrl()
                                .addOnSuccessListener(uri -> databaseReference.child("Users").child(currentUserID).child("image").setValue(uri.toString())
                                        .addOnCompleteListener(task1 -> {
                                            if (!(task1.isSuccessful())) {
                                                //returns error message if uploading image to the firebase database is failed
                                                Toast.makeText(getApplicationContext(), "Error: " + Objects.requireNonNull(task1.getException()).toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }));
                    }
                });
            }
        }
    }

    private void startImageCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setMultiTouchEnabled(true)
                .start(this);
    }



    private void OnDatePicker() {
        //get instance of calendar
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Date Picker
        setupProfileDOB.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileSetupActivity.this,
                    (datePicker, year, month, day) -> {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        setupProfileDOB.setText(date);

                        setDate(year, month, day);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    //set date so on second call remains on the same date that was picked on first call
    private void setDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }


    //User Profile Setup
    private void profileSetup() {
        final String profileName = setupProfileName.getText().toString();
        final String profileDOB = setupProfileDOB.getText().toString();

        if (TextUtils.isEmpty(profileName)){
            setupProfileName.setError("Name required");
            setupProfileName.requestFocus();

        } else if (TextUtils.isEmpty(profileDOB)){
            setupProfileDOB.setError("Date of birth required");
            setupProfileDOB.requestFocus();

        } else {
            final String getFullPhoneNumber = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("username", profileName);
            profileMap.put("dob", profileDOB);
            profileMap.put("contact", getFullPhoneNumber);
            profileMap.put("search", profileName.toLowerCase());
            if (getImageUri() != null) {
                profileMap.put("image", getImageUri());
            }


            databaseReference.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(task -> {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
        }
    }


    // Retrieve User Info if available
    private void retrieveUserInfo() {
        databaseReference.child("Users").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image")) && (snapshot.hasChild("username"))){
                    camera_icon.setVisibility(View.VISIBLE);
                    String getUserProfileImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                    String getUserName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    String getUserDOB = Objects.requireNonNull(snapshot.child("dob").getValue()).toString();

                    camera_icon.setVisibility(View.GONE);
                    Picasso.get().load(getUserProfileImage).into(setupProfileImage);

                    //Storing Image url
                    setImageUri(getUserProfileImage);

                    setupProfileName.setText(getUserName);
                    setupProfileDOB.setText(getUserDOB);

                } else if ((snapshot.exists()) && (snapshot.hasChild("username"))) {

                    String getUserName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    String getUserDOB = Objects.requireNonNull(snapshot.child("dob").getValue()).toString();

                    setupProfileName.setText(getUserName);
                    setupProfileDOB.setText(getUserDOB);

                } else {
                    //Do Nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
                Toast.makeText(ProfileSetupActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void setImageUri(String imageUri){
        this.imageUri = imageUri;
    }

    String getImageUri(){
        return imageUri;
    }


    //get contact permission
    public void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, REQUEST_CODE);

        }
    }

    //get the result of permissions on run time
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            retrieveUserInfo();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        requestPermission();
    }
}