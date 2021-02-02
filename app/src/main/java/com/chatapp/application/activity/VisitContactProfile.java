package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chatapp.application.CheckUserOnlineOfflineState;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class VisitContactProfile extends AppCompatActivity {
    private RelativeLayout buttonBack;
    private TextView contactName, userStatus, contactPhoneNumber, inviteUserTxt, sendMessageTxt;
    private ImageView contactProfileImage;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ShowFriendsActivity showFriendsActivity;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_visit_contact_profile);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        init();

        buttonBack.setOnClickListener(v -> {
            buttonBack.setPressed(true);
            finish();
        });

        getUserProfileInfo();
    }



    @Override
    protected void onStart() {
        super.onStart();

        CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
        userOnlineOfflineState.checkOnlineOrOfflineStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
        userOnlineOfflineState.checkOnlineOrOfflineStatus("Offline");
    }

    @Override
    protected void onResume() {
        super.onResume();

        CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
        userOnlineOfflineState.checkOnlineOrOfflineStatus("Online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
        userOnlineOfflineState.checkOnlineOrOfflineStatus("Offline");
    }



    private void init() {
        buttonBack = findViewById(R.id.contactButtonBack);
        contactProfileImage = findViewById(R.id.contactProfileImage);

        contactName = findViewById(R.id.contactName);
        userStatus = findViewById(R.id.user_status);
        contactPhoneNumber = findViewById(R.id.contactPhoneNumber);

        inviteUserTxt = findViewById(R.id.inviteUserTxt);
        sendMessageTxt = findViewById(R.id.sendMessageTxt);
    }



    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat getCurrentDate = new SimpleDateFormat("MMMM dd");

        return getCurrentDate.format(calendar.getTime());
    }

    private void getUserProfileInfo() {
        String contact_name = getIntent().getStringExtra("contact_username");
        contactName.setText(contact_name);

        String contact_number = getIntent().getStringExtra("contact_number");
        contactPhoneNumber.setText(contact_number);
        contactPhoneNumber.setOnClickListener(v -> contactPhoneNumber.setPressed(true));

        checkUserAvailability(contact_name, contact_number);
    }

    private void checkUserAvailability(final String contact_name, final String contact_number){
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                   final User user = dataSnapshot.getValue(User.class);

                   String userKey = dataSnapshot.getKey();

                   if (!firebaseUser.getUid().equals(userKey)){
                       assert userKey != null;
                       final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userKey);
                       userRef.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               assert user != null;
                               String contact = user.getContact();
                               String contactNumber = contact_number.replaceAll("\\s|-", "");

                               showFriendsActivity = new ShowFriendsActivity();
                               if (user.getContact().equals(contactNumber)
                                       ||  showFriendsActivity.getPhoneNumberWithoutCountryCode(contact).equals(contactNumber)){
                                   inviteUserTxt.setVisibility(View.GONE);

                                   sendMessageTxt.setVisibility(View.VISIBLE);
                                   sendMessageTxt.setOnClickListener(v -> {
                                       sendMessageTxt.setPressed(true);

                                       Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                       intent.putExtra("userId", userKey);
                                       intent.putExtra("username", contact_name);

                                       startActivity(intent);
                                   });

                                   Picasso.get().load(user.getImage()).placeholder(R.drawable.blank_profile_picture).into(contactProfileImage);

                                   userRef.child("userState").addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           String state = Objects.requireNonNull(snapshot.child("state").getValue()).toString();
                                           String date = Objects.requireNonNull(snapshot.child("date").getValue()).toString();
                                           String time = Objects.requireNonNull(snapshot.child("time").getValue()).toString();

                                           detectInternetConnectivity(state, date, time);
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError error) {

                                       }
                                   });
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

    public void detectInternetConnectivity(String state, String date, String time){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null){
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                if (state.equals("Online")){
                    userStatus.setText(state);
                } else {
                    String currentDate = getCurrentDate();
                    String lastSeen;

                    //Converting time if its in 24 hour format
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat _24HourTimeFormat = new SimpleDateFormat("HH:mm");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat _12HourTimeFormat = new SimpleDateFormat("hh:mm a");
                    try {
                        Date _12HourTime = _12HourTimeFormat.parse(time);
                        if (currentDate.equals(date)){
                            if (DateFormat.is24HourFormat(getApplicationContext())){
                                assert _12HourTime != null;
                                lastSeen = "Last seen today at " + _24HourTimeFormat.format(_12HourTime);
                            } else {
                                lastSeen = "Last seen today at " + time;
                            }
                        } else {
                            if (DateFormat.is24HourFormat(getApplicationContext())){
                                assert _12HourTime != null;
                                lastSeen = "Last seen " + date + " at " + _24HourTimeFormat.format(_12HourTime);
                            } else {
                                lastSeen = "Last seen " + date + " at " + time;
                            }
                        }

                        userStatus.setText(lastSeen);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
           userStatus.setVisibility(View.GONE);
        }
    }
}