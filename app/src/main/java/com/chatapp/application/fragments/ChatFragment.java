package com.chatapp.application.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.chatapp.application.adapter.ChatsAdapter;
import com.chatapp.application.model.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment implements ChatsAdapter.ViewHolder.OnItemClickListener {
    private TextView noChatHistoryText;
    private RecyclerView chatListRecyclerView;
    private FloatingActionButton chat_fab;

    private final List<Chat> usersChatList = new ArrayList<>();
    private ChatsAdapter chatsAdapter;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Declaring instance,
        //Initializing
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        noChatHistoryText = view.findViewById(R.id.noChatHistoryText);
        chat_fab = view.findViewById(R.id.chat_fab);
        chat_fab.setOnClickListener(v -> startActivity(new Intent(getContext(), ShowFriendsActivity.class)));


        chatListRecyclerView = view.findViewById(R.id.chatListRecyclerView);
        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getChatLists();
        chatsAdapter = new ChatsAdapter(getContext(), usersChatList, ChatFragment.this);
        chatListRecyclerView.setAdapter(chatsAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void getChatLists(){
        databaseReference.child("Chats").child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!snapshot.exists()){
                    noChatHistoryText.setVisibility(View.VISIBLE);
                } else {
                    noChatHistoryText.setVisibility(View.GONE);

                    Chat chats = snapshot.getValue(Chat.class);
                    usersChatList.add(0, chats);

                    chatsAdapter = new ChatsAdapter(getContext(), usersChatList, ChatFragment.this);
                    chatListRecyclerView.setAdapter(chatsAdapter);
                    chatsAdapter.notifyItemInserted(0);
                    chatsAdapter.notifyDataSetChanged();
                }
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
    public void OnClickItem(int position) {
        String userId = chatsAdapter.getItem(position).getId();

        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("userId", userId);

        databaseReference.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String contact = Objects.requireNonNull(snapshot.child("contact").getValue()).toString();

                Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,null,null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                while (cursor.moveToNext()){
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s|-", "");

                    ShowFriendsActivity showFriendsActivity = new ShowFriendsActivity();
                    if (contact.equals(phoneNumber) || showFriendsActivity.getPhoneNumberWithoutCountryCode(contact).equals(phoneNumber)){
                        intent.putExtra("username", contactName);
                    } else {
                        String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                        intent.putExtra("username", username);
                    }
                }
                cursor.close();


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}