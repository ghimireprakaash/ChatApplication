package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.chatapp.application.R;
import com.chatapp.application.adapter.MessageAdapter;
import com.chatapp.application.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private Toolbar toolbar;
    private TextView userName, userStatus;
    private EditText message;
    ImageButton buttonMessageSend;

    String userId;
    String image;
    String userProfileName;

    MessageAdapter messageAdapter;
    List<Chat> messages;
    RecyclerView messageRecycler;

    DatabaseReference databaseReference;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

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


        userId = getIntent().getStringExtra("userId");
        userProfileName = getIntent().getStringExtra("username");

        buildChatRecycler();

        userName.setText(userProfileName);
        checkUserOnlineOrOfflineState();

//        storeEmptyChatInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();

        checkOnlineOrOfflineStatus("Offline");
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkOnlineOrOfflineStatus("Online");
    }


    private void init() {
        toolbar = findViewById(R.id.toolbar);
        userName = findViewById(R.id.userName);
        userStatus = findViewById(R.id.userStatus);
        message = findViewById(R.id.messageBox);
        buttonMessageSend = findViewById(R.id.buttonMessageSend);

        messageRecycler = findViewById(R.id.messagesRecyclerView);
    }


    private void storeEmptyChatInfo(){
        final DatabaseReference emptyChatRef = FirebaseDatabase.getInstance().getReference().child("ChatList")
                .child("EmptyChatList")
                .child(currentUser.getUid())
                .child(userId);

        String emptyMsg = "No messages";

        emptyChatRef.child("id").setValue(userId);
        emptyChatRef.child("EmptyMessageValue").setValue(emptyMsg);
    }


    private void sendMessage(String sender, String message, String receiver, String messageSentTime){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);
        hashMap.put("receiver", receiver);
        hashMap.put("messageSentTime", messageSentTime);

        databaseReference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("ChatList")
                .child(currentUser.getUid())
                .child(userId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.exists())){
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @SuppressLint("SimpleDateFormat")
    public void onSendButtonClicked(View view) {
        String edit_message = message.getText().toString();
        String messageSentTime;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sentTime;
        if (DateFormat.is24HourFormat(getApplicationContext())){
            sentTime = new SimpleDateFormat("HH:mm");
        } else {
            sentTime = new SimpleDateFormat("hh:mm a");
        }
        messageSentTime = sentTime.format(calendar.getTime());

        if (!edit_message.equals("")){
            sendMessage(currentUser.getUid(), edit_message, userId, messageSentTime);
//            databaseReference.child("ChatList").child("EmptyChatList").child(currentUser.getUid()).child(userId).removeValue();
        }

        message.setText("");
    }

    private void buildChatRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(false);
        messageRecycler.setLayoutManager(layoutManager);

        retrieveMessages(currentUser.getUid(), userId);
    }

    private void retrieveMessages(final String myId, final String correspondingUserId){
        messages = new ArrayList<>();

        databaseReference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();

                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Chat chat = dataSnapshot.getValue(Chat.class);

                        assert chat != null;
                        if ((chat.getReceiver().equals(correspondingUserId)) && (chat.getSender().equals(myId))
                                    || (chat.getReceiver().equals(myId)) && (chat.getSender().equals(correspondingUserId))) {
                            messages.add(chat);
                        }

                        messageAdapter = new MessageAdapter(ChatActivity.this, messages);
                        messageRecycler.setAdapter(messageAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkOnlineOrOfflineStatus(String state){
        String saveCurrentTime, saveCurrentDate;

        saveCurrentTime = getCurrentTime();
        saveCurrentDate = getCurrentDate();

        HashMap<String, Object> userStateMap = new HashMap<>();
        userStateMap.put("time", saveCurrentTime);
        userStateMap.put("date", saveCurrentDate);
        userStateMap.put("state", state);

        databaseReference.child("Users").child(currentUser.getUid()).child("userState").updateChildren(userStateMap);
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat getCurrentTime = new SimpleDateFormat("hh:mm a");

        return getCurrentTime.format(calendar.getTime());
    }

    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat getCurrentDate = new SimpleDateFormat("MMMM dd");

        return getCurrentDate.format(calendar.getTime());
    }

    private void checkUserOnlineOrOfflineState(){
        databaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.child("userState").hasChild("state"))){
                    String state = Objects.requireNonNull(snapshot.child("userState").child("state").getValue()).toString();
                    String date = Objects.requireNonNull(snapshot.child("userState").child("date").getValue()).toString();
                    String time = Objects.requireNonNull(snapshot.child("userState").child("time").getValue()).toString();

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
                } else {
                    userStatus.setText(R.string.offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}