package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chatapp.application.R;
import com.chatapp.application.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class VisitContactProfile extends AppCompatActivity {
    private TextView buttonBack, contactName, contactPhoneNumber, inviteUserTxt, freeMessageTxt;
    private ImageView contactProfileImage;

    String contact_name, contact_number;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ShowFriendsActivity showFriendsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_contact_profile);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        init();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserProfileInfo();
    }

    private void init() {
        buttonBack = findViewById(R.id.contactButtonBack);
        contactProfileImage = findViewById(R.id.contactProfileImage);

        contactName = findViewById(R.id.contactName);
        contactPhoneNumber = findViewById(R.id.contactPhoneNumber);

        inviteUserTxt = findViewById(R.id.inviteUserTxt);
        freeMessageTxt = findViewById(R.id.freeMessageTxt);
    }

    private void getUserProfileInfo() {
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

        checkUserAvailability(contact_number);
    }

    private void checkUserAvailability(final String contact_number){
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                   final User user = dataSnapshot.getValue(User.class);

                   String userKey = dataSnapshot.getKey();
                   DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");

                   if (!firebaseUser.getUid().equals(userKey)){
                       assert userKey != null;
                       userRef.child(userKey).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               assert user != null;
                               String contact = user.getContact();
                               String contactNumber = contact_number.replaceAll("\\s|-", "");

                               showFriendsActivity = new ShowFriendsActivity();
                               if (snapshot.exists() && (snapshot.hasChild("contact"))){
                                   if (user.getContact().equals(contactNumber)
                                           ||  showFriendsActivity.getPhoneNumberWithoutCountryCode(contact).equals(contactNumber)){
                                       Picasso.get().load(user.getImage()).placeholder(R.drawable.blank_profile_picture).into(contactProfileImage);

                                       inviteUserTxt.setVisibility(View.GONE);
                                       freeMessageTxt.setVisibility(View.VISIBLE);
                                   }
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private String getPhoneNumberWithoutCountryCode(String contact){
//        //Creating StringBuilder object
//        StringBuilder reverseNumber = new StringBuilder();
//        //Reversing original contact number that was fetched from the database
//        String reversedContact = new StringBuilder(contact).reverse().toString();
//        reverseNumber.append(reversedContact);
//
//        //From the reversed contact number taking 10digits which removes country code
//        String reverseContactWithoutCode = reversedContact.substring(0, 10);
//
//        //Finally reversing the "reverseContactWithoutCode" which results the actual contact number without country code...
//        String contactWithoutCountryCode = new StringBuilder(reverseContactWithoutCode).reverse().toString();
//        reverseNumber.append(contactWithoutCountryCode);
//
//        return contactWithoutCountryCode;
//    }
}