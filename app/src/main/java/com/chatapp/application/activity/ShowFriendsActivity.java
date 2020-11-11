package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.chatapp.application.R;
import com.chatapp.application.adapter.RetrieveUsersAdapter;
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
    Toolbar toolbar;
    EditText searchEditText;
    RecyclerView registeredUserRecyclerView, showUsersOnSearchRecyclerView;

    CardView newGroupCreateOption;

    List<User> listUsers, showUsersBasedOnSearch;
    RetrieveUsersAdapter adapter;
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
        adapter = new RetrieveUsersAdapter(listUsers, this);

        showUsersBasedOnSearch = new ArrayList<>();
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

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getUid().equals(firebaseUser.getUid())){
                        listUsers.add(user);
                    }
                }

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


    private TextWatcher filter = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            searchUser(s.toString());
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
                showUsersBasedOnSearch.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getUid().equals(firebaseUser.getUid())){
                        showUsersBasedOnSearch.add(user);
                        showUsersOnSearchRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
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