package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chatapp.application.CheckUserOnlineOfflineState;
import com.chatapp.application.R;
import com.chatapp.application.adapter.RetrieveUsersAdapter;
import com.chatapp.application.adapter.ShowSearchResultAdapter;
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
import java.util.Objects;

public class ShowFriendsActivity extends AppCompatActivity implements RetrieveUsersAdapter.ViewHolder.OnItemClickListener,
        ShowSearchResultAdapter.ViewHolder.OnItemClickListener {
    private static final String TAG = "ShowFriendsActivity";

    Toolbar toolbar;
    LinearLayout showContacts;
    TextView horizontalBar, searchIcon, clearText, newGroupCreateOption;
    EditText searchEditText;
    RecyclerView registeredUserRecyclerView, showSearchedUsersRecyclerView;

    List<User> listUsers;
    List<Contacts> listUsersBasedOnSearch = new ArrayList<>();
    List<Contacts> filteredList;


    RetrieveUsersAdapter adapter;
    ShowSearchResultAdapter searchAdapter;
    DatabaseReference userRef;
    FirebaseUser firebaseUser;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_show_friends);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference();


        init();


        registeredUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listUsers = new ArrayList<>();
        retrieveUsers();

        showSearchedUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getUsersForSearch();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)){
                    //set search icon to visible if text field is found empty
                    searchIcon.setVisibility(View.VISIBLE);

                    //set clear text icon visibility to gone
                    clearText.setVisibility(View.GONE);

                } else {
                    //set search icon visibility to gone since we want to make search icon invisible
                    // if text is entered on field
                    searchIcon.setVisibility(View.GONE);


                    //show results on searching users
                    filterUser(s.toString());


                    //set visibility to visible if text change is triggered
                    clearText.setVisibility(View.VISIBLE);

                    //providing behaviour for clear icon to clear all text that is entered...
                    clearText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchEditText.setText("");
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
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



    //Initialization of fields
    private void init(){
        toolbar = findViewById(R.id.chat_fab_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        searchIcon = findViewById(R.id.searchIcon);
        clearText = findViewById(R.id.clearText);
        searchEditText = findViewById(R.id.searchEditText);
        newGroupCreateOption = findViewById(R.id.createNewGroupCardOption);
        horizontalBar = findViewById(R.id.horizontalBar);
        showContacts = findViewById(R.id.showContacts);

        registeredUserRecyclerView = findViewById(R.id.registeredUserRecyclerView);
        showSearchedUsersRecyclerView = findViewById(R.id.showUsersOnSearch_recyclerView);
    }


    //called when createNewGroup is clicked
    public void createNewGroup(View view) {
        newGroupCreateOption.setPressed(true);
        startActivity(new Intent(ShowFriendsActivity.this, NewGroupActivity.class));
        finish();
    }



    private void retrieveUsers() {
        userRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (!user.getUid().equals(firebaseUser.getUid())){
                        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, null, null,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                        while (cursor.moveToNext()) {
                            String contacts = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            String contactNumber = contacts.replaceAll("\\s|-", "");

                            //Retrieving users contact number stored on database
                            String contact = user.getContact();


                            //And, taking those contact number without country code to compare with phone contacts...
                            if (((contact.equals(contactNumber))) || ((getPhoneNumberWithoutCountryCode(contact).equals(contactNumber)))) {
                                horizontalBar.setVisibility(View.VISIBLE);
                                showContacts.setVisibility(View.VISIBLE);

                                listUsers.add(user);
                            }
                        }
                        cursor.close();
                    }
                }

                adapter = new RetrieveUsersAdapter(getApplicationContext(), listUsers, ShowFriendsActivity.this);
                registeredUserRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public String getPhoneNumberWithoutCountryCode(String contact){
        //Creating StringBuilder object
        StringBuilder reverseNumber = new StringBuilder();
        //Reversing original contact number that was fetched from the database
        String reversedContact = new StringBuilder(contact).reverse().toString();
        reverseNumber.append(reversedContact);

        //From the reversed contact number taking 10digits which removes country code
        String reverseContactWithoutCode = reversedContact.substring(0, 10);

        //Finally reversing the "reverseContactWithoutCode" which results the actual contact number without country code...
        String contactWithoutCountryCode = new StringBuilder(reverseContactWithoutCode).reverse().toString();
        reverseNumber.append(contactWithoutCountryCode);

        return contactWithoutCountryCode;
    }



    private void getUsersForSearch(){
        Cursor cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");

        while (cursor.moveToNext()){
            final String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            final String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            userRef.child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);

                        assert user != null;
                        if (!user.getUid().equals(firebaseUser.getUid())){
                            String contact = user.getContact();
                            String phoneContact = contactNumber.replaceAll("\\s|-", "");

                            if (contact.equals(phoneContact) || getPhoneNumberWithoutCountryCode(contact).equals(phoneContact)){
                                String userId = user.getUid();

                                Contacts contacts = new Contacts();
                                contacts.setContact_name(contactName);
                                contacts.setUid(userId);


                                listUsersBasedOnSearch.add(contacts);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        cursor.close();
    }

    //Called to filter users
    private void filterUser(String text) {
        filteredList = new ArrayList<>();

        for (Contacts name : listUsersBasedOnSearch){
            filteredList.clear();
            if (text.toLowerCase().contains(name.getContact_name().toLowerCase())){
                filteredList.add(name);
            }
        }

        searchAdapter = new ShowSearchResultAdapter(this, filteredList, this);
        showSearchedUsersRecyclerView.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
//        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("search")
//                .startAt(s)
//                .endAt(s+"\uf8ff");
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listUsersBasedOnSearch.clear();
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    User user = dataSnapshot.getValue(User.class);
//
//                    assert user != null;
//                    assert firebaseUser != null;
//                    if (!user.getUid().equals(firebaseUser.getUid())){
//                        listUsersBasedOnSearch.add(user);
//                    }
//                }
//
//
//                searchAdapter = new RetrieveUsersAdapter(listUsersBasedOnSearch, ShowFriendsActivity.this);
//                showUsersOnSearchRecyclerView.setAdapter(searchAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }



    //OnItem click for users on contacts
    @Override
    public void OnItemClick(int position) {
        String userId = adapter.getItem(position).getUid();

        Cursor cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (cursor.moveToNext()){
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s|-", "");

            String contact = adapter.getItem(position).getContact();
            if (contact.equals(phoneNumber) || getPhoneNumberWithoutCountryCode(contact).equals(phoneNumber)){
                Intent intent = new Intent(ShowFriendsActivity.this, ChatActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("username", contactName);
                startActivity(intent);
                finish();
            }
        }
        cursor.close();
    }

    //OnItem click for search results
    @Override
    public void OnClickItem(int position) {
        String userId = searchAdapter.getItem(position).getUid();
        String username = searchAdapter.getItem(position).getContact_name();

        Intent intent = new Intent(ShowFriendsActivity.this, ChatActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}