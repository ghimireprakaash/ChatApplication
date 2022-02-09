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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.chatapp.application.CheckUserOnlineOfflineState;
import com.chatapp.application.R;
import com.chatapp.application.SharedPref;
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

public class ProfileUpdate extends AppCompatActivity {
    private final String TAG = "ProfileUpdate";

    private static final int GALLERY_PICK = 1;
    Uri uri;

    //initially setting year, month, and day to 0
    int year = 0;
    int month = 0;
    int day = 0;

    private RelativeLayout buttonBack;
    private ImageView userProfileImage;
    private TextView userProfileNameAsIcon;
    private EditText updateProfileName, updateProfileDateOfBirth;


    StorageReference userProfileStorageRef;
    DatabaseReference databaseReference;
    String currentUserID;

    private SharedPref sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = new SharedPref(this);
        if (sharedPreferences.loadNightMode()){
            setTheme(R.style.AppTheme_DarkMode);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            setTheme(R.style.AppTheme);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_profile_update);

        userProfileStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        init();


        //get instance of calendar
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        updateProfileDateOfBirth.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileUpdate.this,
                    (datePicker, year, month, day) -> {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        updateProfileDateOfBirth.setText(date);

                        setDate(year, month, day);
                    }, year, month, day);
            datePickerDialog.show();
        });



        buttonBack.setOnClickListener(v -> {
            //Updates profile...
            updateProfile();
            buttonBack.setPressed(true);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Updates profile...
        updateProfile();
    }

    private void setDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }


    @Override
    protected void onStart() {
        super.onStart();

        retrieveUserInfo();
        CheckUserOnlineOfflineState checkUserOnlineOfflineState = new CheckUserOnlineOfflineState(this);
        checkUserOnlineOfflineState.checkOnlineOrOfflineStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        CheckUserOnlineOfflineState checkUserOnlineOfflineState = new CheckUserOnlineOfflineState(this);
        checkUserOnlineOfflineState.checkOnlineOrOfflineStatus("Offline");
    }

    @Override
    protected void onResume() {
        super.onResume();

        CheckUserOnlineOfflineState checkUserOnlineOfflineState = new CheckUserOnlineOfflineState(this);
        checkUserOnlineOfflineState.checkOnlineOrOfflineStatus("Online");
    }


    //initializing views
    private void init() {
        buttonBack = findViewById(R.id.buttonBack);
        userProfileImage = findViewById(R.id.userImage);
        userProfileNameAsIcon = findViewById(R.id.userProfileNameAsIcon);
        updateProfileName = findViewById(R.id.updateProfileName);
        updateProfileDateOfBirth = findViewById(R.id.updateProfileDateOfBirth);
    }


    private void retrieveUserInfo(){
        databaseReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image"))){
                    String profileImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                    String profileName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    String userDOB = Objects.requireNonNull(snapshot.child("dob").getValue()).toString();

                    Picasso.get().load(profileImage).into(userProfileImage);
                    userProfileNameAsIcon.setVisibility(View.GONE);

                    updateProfileName.setText(profileName);
                    updateProfileDateOfBirth.setText(userDOB);
                } else {
                    userProfileNameAsIcon.setVisibility(View.VISIBLE);

                    String profileName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    String userDOB = Objects.requireNonNull(snapshot.child("dob").getValue()).toString();

                    String[] nameParts = profileName.split(" ");
                    String firstName = nameParts[0];
                    String firstNameChar = firstName.substring(0, 1).toUpperCase();

                    if (nameParts.length > 1){
                        String lastName = nameParts[nameParts.length - 1];
                        String lastNameChar = lastName.substring(0, 1).toUpperCase();

                        String fullNameChar = firstNameChar + lastNameChar;

                        //sets user profile image name if image isn't available
                        userProfileNameAsIcon.setText(fullNameChar);
                    } else {
                        //sets user profile image name if user has only first name and not last name
                        userProfileNameAsIcon.setText(firstNameChar);
                    }


                    updateProfileName.setText(profileName);
                    updateProfileDateOfBirth.setText(userDOB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
                Toast.makeText(ProfileUpdate.this, "Error: "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    //Setup profile image if profile was not added on first time profile setup
    public void updateProfileImage(View view) {
        CropImage.startPickImageActivity(ProfileUpdate.this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)){
                uri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICK);
            }else {
                startImageCrop(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                assert result != null;
                Uri resultUri = result.getUri();

                //setting image uri to user profile image view
                userProfileImage.setImageURI(resultUri);

                final StorageReference filePath = userProfileStorageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()){

                                String message = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(getApplicationContext(), "Error uploading "+message, Toast.LENGTH_LONG).show();

                            }else {
                                filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Log.d(TAG, "onSuccess: downloadUrl: "+uri.toString());

                                    databaseReference.child("Users").child(currentUserID).child("image")
                                            .setValue(uri.toString())
                                            .addOnFailureListener(e -> {
                                                Log.d(TAG, "onFailure: "+e.getMessage());
                                                Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                });
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


    private void updateProfile(){
        String profileName = updateProfileName.getText().toString();
        String profileDOB = updateProfileDateOfBirth.getText().toString();

        if (TextUtils.isEmpty(profileName)){
            updateProfileName.setError("Can't be left empty");
            updateProfileName.requestFocus();
        } else if (TextUtils.isEmpty(profileDOB)){
            updateProfileDateOfBirth.setError("Can't be left empty");
            updateProfileDateOfBirth.requestFocus();
        } else {
            HashMap<String, Object> updateProfileMap = new HashMap<>();
            updateProfileMap.put("username", profileName);
            updateProfileMap.put("dob", profileDOB);
            updateProfileMap.put("search", profileName.toLowerCase());

            databaseReference.child("Users").child(currentUserID).updateChildren(updateProfileMap);
        }
    }
}