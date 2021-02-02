package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.chatapp.application.CheckUserOnlineOfflineState;
import com.chatapp.application.R;
import com.chatapp.application.adapter.MessageAdapter;
import com.chatapp.application.model.Chat;
import com.chatapp.application.model.User;
import com.chatapp.application.notifications.APIService;
import com.chatapp.application.notifications.Client;
import com.chatapp.application.notifications.Data;
import com.chatapp.application.notifications.Response;
import com.chatapp.application.notifications.Sender;
import com.chatapp.application.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private Toolbar toolbar;
    private TextView userName, userStatus;
    private EditText message;
    ImageButton buttonMessageSend;

    String userId;
    String userProfileName;

    private MessageAdapter messageAdapter;
    private final List<Chat> messages = new ArrayList<>();
    private RecyclerView messageRecycler;

    DatabaseReference databaseReference;
    FirebaseUser currentUser;



    APIService apiService;
    boolean notify = false;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_chat);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        init();


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> finish());

        //Get userId and userProfileName with the help of intent
        userId = getIntent().getStringExtra("userId");
        userProfileName = getIntent().getStringExtra("username");
        //set username to its holder place with whom chatting is being held
        userName.setText(userProfileName);

        //Check user online or offline status
        checkUserOnlineOrOfflineState();

        //build chat recyclerview layout to display list of messages between users
        buildChatRecycler();


        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);




        buttonMessageSend.setOnClickListener(v -> {
            notify = true;
            sendMessage();
            message.setText("");
        });
    }



    private void init() {
        toolbar = findViewById(R.id.toolbar);
        userName = findViewById(R.id.userName);
        userStatus = findViewById(R.id.userStatus);
        message = findViewById(R.id.messageBox);
        buttonMessageSend = findViewById(R.id.buttonMessageSend);

        messageRecycler = findViewById(R.id.messagesRecyclerView);
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


    private void sendMessage(){
        final String messageText = message.getText().toString();
        if (!messageText.isEmpty()){
            String messageSenderRef = "Messages/" + currentUser.getUid() + "/" + userId;
            String messageReceiverRef = "Messages/" + userId + "/" + currentUser.getUid();

            DatabaseReference messageKeyRef = databaseReference.child("Messages")
                    .child(currentUser.getUid()).child(userId).push();

            String messagesPushId = messageKeyRef.getKey();

            //get message sent time from sender side...
            String messageSentTime;
            messageSentTime = getCurrentTime();


            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put("message", messageText);
            messageMap.put("sender", currentUser.getUid());
            messageMap.put("receiver", userId);
            messageMap.put("messageSentTime", messageSentTime);

            HashMap<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagesPushId, messageMap);
            messageBodyDetails.put(messageReceiverRef + "/" + messagesPushId, messageMap);

            databaseReference.updateChildren(messageBodyDetails);

            //store chats id with whom users had a conversation
            databaseReference.child("Chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DatabaseReference addUserRef = FirebaseDatabase.getInstance().getReference().child("Chats");
                    addUserRef.child(currentUser.getUid()).child(userId).child("id").setValue(userId);
                    addUserRef.child(userId).child(currentUser.getUid()).child("id").setValue(currentUser.getUid());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference sendNotificationRef = databaseReference.child("Users").child(currentUser.getUid());
            sendNotificationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if (notify){
                        assert user != null;
                        sendNotificationToUser(userId, user.getUsername(), messageText);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void sendNotificationToUser(final String userId, final String username, final String messageText) {
        DatabaseReference tokens = databaseReference.child("Tokens");
        Query query = tokens.orderByKey().equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(R.mipmap.ic_launcher, "Viber Lite", currentUser.getUid(), username+":"+messageText, userId);

                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void buildChatRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(false);
        messageRecycler.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(getApplicationContext(), messages, userId);
        messageRecycler.setAdapter(messageAdapter);
    }


    private void checkUserOnlineOrOfflineState(){
        databaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.child("userState").hasChild("state"))){
                    String state = Objects.requireNonNull(snapshot.child("userState").child("state").getValue()).toString();
                    String date = Objects.requireNonNull(snapshot.child("userState").child("date").getValue()).toString();
                    String time = Objects.requireNonNull(snapshot.child("userState").child("time").getValue()).toString();

                    detectInternetConnectivity(state, date, time);

                } else {
                    userStatus.setText(R.string.offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void detectInternetConnectivity(String state, String date, String time){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null){
            userStatus.setVisibility(View.GONE);
        } else {
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
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        CheckUserOnlineOfflineState userOnlineOfflineState = new CheckUserOnlineOfflineState(getApplicationContext());
        userOnlineOfflineState.checkOnlineOrOfflineStatus("Online");

        databaseReference.child("Messages").child(currentUser.getUid()).child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Chat chats = snapshot.getValue(Chat.class);
                messages.add(chats);

                messageAdapter.notifyDataSetChanged();

                messageRecycler.smoothScrollToPosition(messageAdapter.getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
}