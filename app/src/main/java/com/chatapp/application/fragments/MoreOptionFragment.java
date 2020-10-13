package com.chatapp.application.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.chatapp.application.profile.ProfileUpdate;
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
import static android.app.Activity.RESULT_OK;

public class MoreOptionFragment extends Fragment {
    //initializing request code to 1
    private static final int GALLERY_PICK = 1;

    private TextView editOption;

    private ImageView userProfileImage;
    private TextView camera_icon;

    private TextView userProfileName, userProfileContact, userDOB;


    //Declaring Firebase Instance
    private FirebaseAuth firebaseAuth;
    private String getCurrentUserID;
    private DatabaseReference databaseReference;
    private StorageReference userProfileStorageRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_more_option, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initializing firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Getting Current user id
        getCurrentUserID = firebaseAuth.getCurrentUser().getUid();

        userProfileStorageRef = FirebaseStorage.getInstance().getReference().child(getCurrentUserID).child("Profile Images");



        //Initializing fields
        editOption = view.findViewById(R.id.editOption);

        userProfileImage = view.findViewById(R.id.profileImage);
        camera_icon = view.findViewById(R.id.camera_icon);

        userProfileName = view.findViewById(R.id.profileName);
        userProfileContact = view.findViewById(R.id.profileContactNumber);
        userDOB = view.findViewById(R.id.profileDOB);


        editOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProfileUpdate.class));
                Objects.requireNonNull(getActivity()).finish();
            }
        });


        userProfileImage.isPressed();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getActivity(), ProfileUpdate.class);
                startActivity(startIntent);
//                addProfileImage();
            }
        });
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
                    .setAspectRatio(4,3)
                    .start(Objects.requireNonNull(this.getActivity()));
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                assert result != null;
                Uri resultUri = result.getUri();

                //removing visibility of camera icon after successful image has been uploaded to user profile
                camera_icon.setVisibility(View.GONE);

                //setting image uri to user profile image view
                userProfileImage.setImageURI(resultUri);


                if (userProfileStorageRef == null){
                    StorageReference filePath = userProfileStorageRef.child(getCurrentUserID + ".jpg");
                    filePath.putFile(resultUri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (!task.isSuccessful()){

                                        String message = Objects.requireNonNull(task.getException()).getMessage();
                                        Toast.makeText(getActivity(), "Error uploading "+message, Toast.LENGTH_LONG).show();

                                    }else {
                                        String downloadUri = Objects.requireNonNull(task.getResult()).getStorage().getDownloadUrl().toString();

                                        databaseReference.child("Users").child(getCurrentUserID).child("userProfileImage")
                                                .setValue(downloadUri)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()){
                                                            String message = task.getException().getMessage();
                                                            Toast.makeText(getActivity(), "Error: "+message, Toast.LENGTH_LONG).show();
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



    //Retrieving Current User Information
    private void retrieveUserInfo() {
        databaseReference.child("Users").child(getCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userProfileImage")) && (snapshot.hasChild("username"))){

                    String getUserProfileImage = snapshot.child("userProfileImage").getValue().toString();
                    String getUserName = snapshot.child("username").getValue().toString();
                    String getUserContactNumber = snapshot.child("contact").getValue().toString();
                    String getUserDOB = snapshot.child("dob").getValue().toString();


                    Picasso.get().load(getUserProfileImage).into(userProfileImage);
                    userProfileName.setText(getUserName);
                    userProfileContact.setText(getUserContactNumber);
                    userDOB.setText(getUserDOB);

                } else if ((snapshot.exists() && (snapshot.hasChild("username")))){

                    String getUserName = snapshot.child("username").getValue().toString();
                    String getUserContactNumber = snapshot.child("contact").getValue().toString();
                    String getUserDOB = snapshot.child("dob").getValue().toString();

                    userProfileName.setText(getUserName);
                    userProfileContact.setText(getUserContactNumber);
                    userDOB.setText(getUserDOB);

                } else {
                    //DO Nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();

        //retrieves user info on start of the fragment
        retrieveUserInfo();
    }
}