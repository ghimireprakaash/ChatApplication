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
import com.chatapp.application.activity.ShowFriendsActivity;
import com.chatapp.application.adapter.RetrieveUsersRecyclerViewAdapter;
import com.chatapp.application.model.Contacts;
import com.chatapp.application.profile.ProfileSetupActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class ChatFragment extends Fragment {
    private TextView noChatHistoryText;
    private RecyclerView chatRecyclerView;
    private FloatingActionButton chat_fab;

    private RetrieveUsersRecyclerViewAdapter adapter;
    private List<Contacts> listUsers;


    private List<String> usersList;

    private DatabaseReference databaseReference;

    //Declaring string variable currentUserID
    private String currentUserID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        View view = inflater.inflate(R.layout.fragment_chat, container, false);


        //Declaring instances and,
        //Initializing
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();


        //verifies if current user is null or not null
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(getContext(), ProfileSetupActivity.class));
        } else {
            verifyUserExistence();
        }


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noChatHistoryText = view.findViewById(R.id.noChatHistoryText);
        chat_fab = view.findViewById(R.id.chat_fab);
        chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ShowFriendsActivity.class));
            }
        });

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);


        usersList = new ArrayList<>();
        getMessages();

    }


    //To verify user existence
    private void verifyUserExistence() {
        databaseReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
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


    private void getMessages(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("UsersChatList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                usersList.clear();
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Chat chat = dataSnapshot.getValue(Chat.class);
//
//                    assert chat != null;
//                    if (chat.getSender().equals(currentUserID)){
//                        usersList.add(chat.getReceiver());
//                    }
//                    if (chat.getReceiver().equals(currentUserID)){
//                        usersList.add(chat.getSender());
//                    }
//                }
//
//                readChats();
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
                    Contacts userContact = dataSnapshot.getValue(Contacts.class);

                    for (String id : usersList){
                        assert userContact != null;
                        if (userContact.getUid().equals(id)){
                            if (listUsers.size() != 0){
                                for (Contacts userContact1 : listUsers){
                                    if (!userContact.getUid().equals(userContact1.getUid())){
                                        listUsers.add(userContact);
                                    }
                                }
                            } else {
                                listUsers.add(userContact);
                            }
                        }
                    }
                }

                FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(databaseReference, Contacts.class)
                        .build();

                adapter = new RetrieveUsersRecyclerViewAdapter(options);
                noChatHistoryText.setVisibility(View.GONE);
                chatRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}