package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.chatapp.application.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView userName, seenStatus;
    private EditText message;
    ImageButton buttonMessageSend;

    String userId;
    Intent intent;

    DatabaseReference reference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = FirebaseAuth.getInstance().getCurrentUser();

        init();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
                finish();
            }
        });


        intent = getIntent();

        userId = intent.getStringExtra("userId");

        final String getUserName = intent.getStringExtra("username");
        userName.setText(getUserName);


        storeChatInfo();


        //On pressed button send allows user to send message to the destination user
        buttonMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit_message = message.getText().toString();
                if (!edit_message.equals("")){
                    sendMessage(user.getUid(), edit_message, userId);
                }

                message.setText("");
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        userName = findViewById(R.id.userName);
        seenStatus = findViewById(R.id.seenStatus);
        message = findViewById(R.id.messageBox);
        buttonMessageSend = findViewById(R.id.buttonMessageSend);
    }


    private void storeChatInfo(){
        String userName = intent.getStringExtra("username");

        reference = FirebaseDatabase.getInstance().getReference();
        assert userId != null;
        reference.child("UsersChatList").child(userId).child("username").setValue(userName);
    }


    private void sendMessage(String sender, String message, String receiver){
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);
        hashMap.put("receiver", receiver);

        reference.child("UsersChatList").push().setValue(hashMap);
    }
}