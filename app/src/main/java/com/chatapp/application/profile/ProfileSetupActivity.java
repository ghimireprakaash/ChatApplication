package com.chatapp.application.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.chatapp.application.R;
import com.chatapp.application.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private final String TAG = "ProfileSetupActivity";

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

    //declaring current user id variable
    private String currentUserID;


    //Firebase storage
    private StorageReference userProfileImagesStorageRef;
    private StorageReference filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        //Initializing Instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userProfileImagesStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();



        init();



        setupProfileImage.setPressed(false);

        //Image setup via gallery access
        setupProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupProfileImage();
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
        camera_icon = findViewById(R.id.camera_icon);

        setupProfileName = findViewById(R.id.setupProfileName);
        setupProfileDOB = findViewById(R.id.setupProfileDOB);

        btnSet = findViewById(R.id.buttonSet);
    }



    public void OnDatePicker(View view) {
        //get instance of calendar
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Date Picker
        setupProfileDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileSetupActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month = month+1;
                                String date = day+"/"+month+"/"+year;
                                setupProfileDOB.setText(date);

                                setDate(year, month, day);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    //set date so on second call remains on the same date that was picked on first call
    private void setDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    private void setupProfileImage() {
        CropImage.startPickImageActivity(ProfileSetupActivity.this);
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
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()){
                            String exception = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(getApplicationContext(), "Error selecting image "+ exception, Toast.LENGTH_LONG).show();
                        }
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
            final String getPhoneNumber = getIntent().getStringExtra("phoneNumber");

            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("username", profileName);
            profileMap.put("dob", profileDOB);
            profileMap.put("fullcontactnumber", getFullPhoneNumber);
            profileMap.put("contact", getPhoneNumber);
            profileMap.put("search", profileName.toLowerCase());


            filePath.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.child("Users").child(currentUserID).child("image").setValue(uri.toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!(task.isSuccessful())){
                                                //returns error message if uploading image to the firebase database is failed
                                                Toast.makeText(getApplicationContext(), "Error: " + Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+e.getMessage());
                        }
                    });

            databaseReference.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }


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



    @Override
    protected void onStart() {
        super.onStart();

        retrieveUserInfo();
    }
}