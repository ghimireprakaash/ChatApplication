package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.chatapp.application.R;
import com.chatapp.application.adapter.MessageAdapter;
import com.chatapp.application.model.Chat;
import com.chatapp.application.model.Contacts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView userName, lastSeenStatus;
    private EditText message;
    ImageButton buttonMessageSend;

    String userId;
    String image;
    Intent intent;

    DatabaseReference reference;
    FirebaseUser user;

    MessageAdapter messageAdapter;
    List<Chat> chatList;
    RecyclerView messageRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

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
        image = intent.getStringExtra("image");


        storeChatInfo();

        buildChatRecycler();

        reference.child("UsersChatList").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Contacts model = snapshot.getValue(Contacts.class);
                assert model != null;
                userName.setText(model.getUsername());

                retrieveMessages(user.getUid(), userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        userName = findViewById(R.id.userName);
        lastSeenStatus = findViewById(R.id.seenStatus);
        message = findViewById(R.id.messageBox);
        buttonMessageSend = findViewById(R.id.buttonMessageSend);

        messageRecycler = findViewById(R.id.messagesRecyclerView);
        messageRecycler.setHasFixedSize(true);
    }


    private void storeChatInfo(){
        final String userName = intent.getStringExtra("username");


        assert userId != null;
        reference.child("UsersChatList").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chat = new Chat();

                String username = chat.setUsername(userName);
                reference.child("UsersChatList").child(userId).child("username").setValue(username);

                String emptyMsg = "No messages";
                chat.setEmptyMessageChat(emptyMsg);
                reference.child("UsersChatList").child(userId).child("EmptyMessageValue").setValue(emptyMsg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage(String sender, String message, String receiver, String image){
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);
        hashMap.put("receiver", receiver);
        hashMap.put("image", image);

        reference.child("UsersChatList").child(userId).push().setValue(hashMap);
    }


    public void onSendButtonClicked(View view) {
        //On pressed button send allows user to send message to the destination user
        buttonMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit_message = message.getText().toString();
                if (!edit_message.equals("")){
                    sendMessage(user.getUid(), edit_message, userId, image);
                }

                message.setText("");
            }
        });
    }

    private void buildChatRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setStackFromEnd(true);
        messageRecycler.setLayoutManager(layoutManager);
    }

    private void retrieveMessages(final String myId, final String userid){
        chatList = new ArrayList<>();

        reference.child("UsersChatList").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    assert chat != null;
                    if ((chat.getReceiver().equals(myId)) && (chat.getSender().equals(userid)) ||
                            (chat.getReceiver().equals(userid)) && (chat.getSender().equals(myId))) {
                        chatList.add(chat);
                    }

                    messageAdapter = new MessageAdapter(ChatActivity.this, chatList);
                    messageRecycler.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}