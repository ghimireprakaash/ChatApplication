package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import com.chatapp.application.CheckUserOnlineOfflineState;
import com.chatapp.application.R;
import com.chatapp.application.SharedPref;
import com.chatapp.application.adapter.MultipleContactSelectAdapter;
import com.chatapp.application.model.Contacts;
import com.chatapp.application.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class NewGroupActivity extends AppCompatActivity {
    ImageView buttonBack, createNewGroupBtn;
    RecyclerView contactsRecyclerView, selectedContactsRecyclerView;

    List<Contacts> friendsContactList;
    MultipleContactSelectAdapter contactSelectAdapter;
    String getUserId;


    FirebaseUser firebaseUser;
    DatabaseReference friendsRef;


    private SharedPref sharedPreferences;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = new SharedPref(this);
        if (sharedPreferences.loadNightMode()){
            setTheme(R.style.AppTheme_DarkMode);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        } else {
            setTheme(R.style.AppTheme);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_new_group);


        init();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Users");

//        getContacts();
//        buildContactsRecyclerView();

        createNewGroupBtn.setOnClickListener(v -> {
        });
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

    private void init(){
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        createNewGroupBtn = findViewById(R.id.createNewGroupBtn);
    }


    private void getContacts(){
        friendsContactList = new ArrayList<>();

        Cursor cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        while (cursor.moveToNext()){
            final String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            final String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            friendsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);

                        assert user != null;
                        if (!user.getUid().equals(firebaseUser.getUid())){
                            String contact = user.getContact();
                            String phoneContact = contactNumber.replaceAll("\\s|-", "");

                            ShowFriendsActivity showFriendsActivity = new ShowFriendsActivity();
                            if (contact.equals(phoneContact) ||
                                    showFriendsActivity.getPhoneNumberWithoutCountryCode(contact).equals(phoneContact)){
                                getUserId = user.getUid();

                                Contacts contacts = new Contacts();
                                contacts.setUid(getUserId);
                                contacts.setContact_name(contactName);


                                friendsContactList.add(contacts);
                            }
                        }
                    }

                    contactSelectAdapter = new MultipleContactSelectAdapter(getApplicationContext(), friendsContactList, getUserId);
                    contactsRecyclerView.setAdapter(contactSelectAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        cursor.close();
    }

    private void buildContactsRecyclerView() {
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        contactsRecyclerView.setLayoutManager(layoutManager);

        selectedContactsRecyclerView = findViewById(R.id.selectedContactsRecyclerView);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        layoutManager1.setOrientation(RecyclerView.HORIZONTAL);
        selectedContactsRecyclerView.setLayoutManager(layoutManager1);
    }
}