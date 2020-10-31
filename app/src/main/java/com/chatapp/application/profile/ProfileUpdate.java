package com.chatapp.application.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.chatapp.application.R;
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
import java.util.Objects;

public class ProfileUpdate extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;
    Uri uri;

    private RelativeLayout buttonBack;
    private ImageView userProfileImage;
    private EditText updateProfileName, updateProfileDateOfBirth;


    StorageReference userProfileStorageRef;
    DatabaseReference databaseReference;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        userProfileStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        init();



        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonBack.setPressed(true);
                finish();
            }
        });



        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileImage();
            }
        });
    }


    //initializing views
    private void init() {
        buttonBack = findViewById(R.id.buttonBack);
        userProfileImage = findViewById(R.id.userImage);
        updateProfileName = findViewById(R.id.updateProfileName);
        updateProfileDateOfBirth = findViewById(R.id.updateProfileDateOfBirth);
    }



    //Setup profile image if profile was not added on first time profile setup
    private void updateProfileImage() {
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

                StorageReference filePath = userProfileStorageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (!task.isSuccessful()){

                                    String message = Objects.requireNonNull(task.getException()).getMessage();
                                    Toast.makeText(getApplicationContext(), "Error uploading "+message, Toast.LENGTH_LONG).show();

                                }else {
                                    String downloadUrl = Objects.requireNonNull(task.getResult()).getStorage().getDownloadUrl().toString();

                                    databaseReference.child("Users").child(currentUserID).child("image")
                                            .setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()){
                                                        String message = Objects.requireNonNull(task.getException()).getMessage();
                                                        Toast.makeText(getApplicationContext(), "Error: "+message, Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
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


    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.child("Users").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                String profileName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                String userDOB = Objects.requireNonNull(snapshot.child("dob").getValue()).toString();

                Picasso.get().load(profileImage).into(userProfileImage);
                updateProfileName.setText(profileName);
                updateProfileDateOfBirth.setText(userDOB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
                Toast.makeText(ProfileUpdate.this, "Error: "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}