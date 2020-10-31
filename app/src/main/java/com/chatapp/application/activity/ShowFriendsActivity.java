package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.chatapp.application.R;
import com.chatapp.application.adapter.RetrieveUsersRecyclerViewAdapter;
import com.chatapp.application.model.Contacts;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ShowFriendsActivity extends AppCompatActivity implements RetrieveUsersRecyclerViewAdapter.ViewHolder.OnItemClickListener {
    Toolbar toolbar;
    EditText searchEditText;
    RecyclerView registeredUserRecyclerView;

    CardView newGroupCreateOption;

    RetrieveUsersRecyclerViewAdapter adapter;
    FirebaseRecyclerOptions<Contacts> options;
    DatabaseReference userRef, reference;
    String currentUser;

    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_friends);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        init();


        String getCurrentUser = userRef.child(currentUser).toString();
        if (getCurrentUser.equals(currentUser)){
//            userRef.child("Users").child(currentUser);
        }

        registeredUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        options = new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(userRef, Contacts.class)
                        .build();

        adapter = new RetrieveUsersRecyclerViewAdapter(options, this);
        registeredUserRecyclerView.setAdapter(adapter);
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


    private void filter(){

    }


    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    public void OnItemClick(int position) {
        String userId = adapter.getItem(position).getUid();
        String userName = adapter.getItem(position).getUsername();
        String image = adapter.getItem(position).getImage();
        String phoneNumber = adapter.getItem(position).getContact();

        Intent intent = new Intent(ShowFriendsActivity.this, ChatActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", userName);
        intent.putExtra("image", image);
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
        finish();
    }
}