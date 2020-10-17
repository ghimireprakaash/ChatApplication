package com.chatapp.application.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.chatapp.application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.Objects;

public class ProfileUpdate extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;


    private TextView buttonBack;
    private ImageView userProfileImage;


    StorageReference userProfileStorageRef;
    DatabaseReference databaseReference;
    String getCurrentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        userProfileStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getCurrentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        init();



        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfileImage();
            }
        });
    }


    //initializing views
    private void init() {
        buttonBack = findViewById(R.id.buttonBack);
        userProfileImage = findViewById(R.id.userImage);
    }



    //Setup profile image if profile was not added on first time profile setup
    private void addProfileImage() {
        Intent addProfileImageIntent = new Intent();
        addProfileImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        addProfileImageIntent.setType("image/*");

        startActivityForResult(addProfileImageIntent, GALLERY_PICK);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(7,7)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                assert result != null;
                Uri resultUri = result.getUri();

                //setting image uri to user profile image view
                userProfileImage.setImageURI(resultUri);

                StorageReference filePath = userProfileStorageRef.child(getCurrentUserID + ".jpg");
                filePath.putFile(resultUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (!task.isSuccessful()){

                                    String message = Objects.requireNonNull(task.getException()).getMessage();
                                    Toast.makeText(getApplicationContext(), "Error uploading "+message, Toast.LENGTH_LONG).show();

                                }else {
                                    String downloadUrl = Objects.requireNonNull(task.getResult()).getStorage().getDownloadUrl().toString();

                                    databaseReference.child("Users").child(getCurrentUserID).child("image")
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
}