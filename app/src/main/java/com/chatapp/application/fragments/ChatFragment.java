package com.chatapp.application.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.activity.ChatActivity;
import com.chatapp.application.activity.ShowFriendsActivity;
import com.chatapp.application.adapter.RetrieveUsersAdapter;
import com.chatapp.application.model.Chat;
import com.chatapp.application.model.User;
import com.chatapp.application.profile.ProfileSetupActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class ChatFragment extends Fragment implements RetrieveUsersAdapter.ViewHolder.OnItemClickListener {
    private TextView noChatHistoryText;
    private RecyclerView chatRecyclerView;
    private FloatingActionButton chat_fab;

    private RetrieveUsersAdapter adapter;
    private List<User> listUsers;

    private List<String> usersList;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    //Declaring string variable
    private String userId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Declaring instances and,
        //Initializing
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        //verifies if current user is null or not null
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(getContext(), ProfileSetupActivity.class));
        } else {
            verifyUserExistence();
        }


        noChatHistoryText = view.findViewById(R.id.noChatHistoryText);

        chat_fab = view.findViewById(R.id.chat_fab);
        chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ShowFriendsActivity.class));
            }
        });

        
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        getChat();
    }


    //To verify user existence
    private void verifyUserExistence() {
        databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("username").exists())){
                    // If username doesn't exists then moves to profile setup
                    try {
                        Intent intent = new Intent(getContext(), ProfileSetupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getChat(){
        usersList = new ArrayList<>();

        databaseReference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usersList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Chat chat = dataSnapshot.getValue(Chat.class);

                        noChatHistoryText.setVisibility(View.GONE);

                        assert chat != null;
                        if (chat.getSender().equals(currentUser.getUid())) {
                            usersList.add(chat.getReceiver());
                        }
                        if (chat.getReceiver().equals(currentUser.getUid())) {
                            usersList.add(chat.getSender());
                        }
                    }
                } else {
                    noChatHistoryText.setVisibility(View.VISIBLE);
                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats() {
        listUsers = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    //Display user with whom chat is going on
                    for (String id : usersList){
                        assert user != null;
                        if (user.getUid().equals(id)){
                            if (listUsers.size() != 0){
                                for (User user1 : listUsers){
                                    if (!user.getUid().equals(user1.getUid())){
                                        listUsers.add(user);
                                    }
                                }
                            } else {
                                listUsers.add(user);
                            }
                        }
                    }
                }

                adapter = new RetrieveUsersAdapter(listUsers, (RetrieveUsersAdapter.ViewHolder.OnItemClickListener) getContext());
                chatRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        startActivity(intent);
    }
}