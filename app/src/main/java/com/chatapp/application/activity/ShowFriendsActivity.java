package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chatapp.application.R;
import com.chatapp.application.adapter.RetrieveUsersAdapter;
import com.chatapp.application.model.Contacts;
import com.chatapp.application.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowFriendsActivity extends AppCompatActivity implements RetrieveUsersAdapter.ViewHolder.OnItemClickListener {
    private static final String TAG = "ShowFriendsActivity";

    Toolbar toolbar;
    LinearLayout showContacts;
    TextView searchIcon, clearText;
    EditText searchEditText;
    RecyclerView registeredUserRecyclerView, showUsersOnSearchRecyclerView;

    CardView newGroupCreateOption;

    List<User> listUsers, listUsersBasedOnSearch;
    RetrieveUsersAdapter adapter, searchAdapter;
    DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_friends);

        userRef = FirebaseDatabase.getInstance().getReference();


        init();


        registeredUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listUsers = new ArrayList<>();
        retrieveUsers();


        showUsersOnSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listUsersBasedOnSearch = new ArrayList<>();
        searchEditText.addTextChangedListener(filter);
    }

    //Initialization of fields
    private void init(){
        toolbar = findViewById(R.id.chat_fab_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchIcon = findViewById(R.id.searchIcon);
        clearText = findViewById(R.id.clearText);
        searchEditText = findViewById(R.id.searchEditText);
        newGroupCreateOption = findViewById(R.id.createNewGroupCardOption);

        registeredUserRecyclerView = findViewById(R.id.registeredUserRecyclerView);
        showUsersOnSearchRecyclerView = findViewById(R.id.showUsersOnSearch_recyclerView);
    }


    private void retrieveUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, null, null, null);
                    while (cursor.moveToNext()) {
                        String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        assert user != null;
//                        if (user.getContact().equals(contactNumber)) {
//                            listUsers.add(user);
//                        }

                        if (user.getFullcontactnumber().equals(contactNumber)){
                            listUsers.add(user);
                        }
                    }
                    cursor.close();
                }

                adapter = new RetrieveUsersAdapter(listUsers, ShowFriendsActivity.this);
                registeredUserRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    //called when createNewGroup is clicked
    public void createNewGroup(View view) {
        newGroupCreateOption.setPressed(true);
        newGroupCreateOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    private final TextWatcher filter = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String valueChange = searchEditText.getText().toString();

            if ((TextUtils.isEmpty(valueChange)) || (TextUtils.isEmpty(valueChange))){
                //set search icon to visible if text field is found empty
                searchIcon.setVisibility(View.VISIBLE);

                //set clear text icon visibility to gone
                clearText.setVisibility(View.GONE);

            } else {
                //set search icon visibility to gone since we want to make search icon invisible
                // if text is entered on field
                searchIcon.setVisibility(View.GONE);


                //show results on searching users
                searchUser(s.toString());


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
    };

    private void searchUser(String s) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsersBasedOnSearch.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getUid().equals(firebaseUser.getUid())){
                        listUsersBasedOnSearch.add(user);
                    }
                }


                searchAdapter = new RetrieveUsersAdapter(listUsersBasedOnSearch, ShowFriendsActivity.this);
                showUsersOnSearchRecyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void OnItemClick(int position) {
        String userId = adapter.getItem(position).getUid();
        String userName = adapter.getItem(position).getUsername();
        String image = adapter.getItem(position).getImage();

        Intent intent = new Intent(ShowFriendsActivity.this, ChatActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", userName);
        intent.putExtra("image", image);
        startActivity(intent);
        finish();
    }
}