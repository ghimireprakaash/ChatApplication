package com.chatapp.application.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.activity.ShowFriendsActivity;
import com.chatapp.application.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Objects;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    Context context;
    private final List<Chat> list;

    private DatabaseReference usersRef, messageRef;
    ViewHolder.OnItemClickListener onItemClickListener;



    public ChatsAdapter(Context context, List<Chat> list, ViewHolder.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_layout, parent, false);
        return new ChatsAdapter.ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Chat getPosition = list.get(position);
        String userId = getPosition.getId();

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String contact = Objects.requireNonNull(snapshot.child("contact").getValue()).toString();

                Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,null,null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                while (cursor.moveToNext()) {
                    String phoneContact = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s|-", "");
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    ShowFriendsActivity showFriendsActivity = new ShowFriendsActivity();
                    if (contact.equals(phoneContact) || showFriendsActivity.getPhoneNumberWithoutCountryCode(contact).equals(phoneContact)){
                        holder.userName.setText(contactName);

                        if (snapshot.hasChild("image")){
                            String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();

                            Picasso.get().load(image).into(holder.userProfile);
                        } else {
                            String[] nameParts = contactName.split(" ");
                            String firstName = nameParts[0];
                            String firstNameChar = firstName.substring(0, 1).toUpperCase();

                            if (nameParts.length > 1){
                                String lastName = nameParts[nameParts.length - 1];
                                String lastNameChar = lastName.substring(0, 1).toUpperCase();

                                String fullNameChar = firstNameChar + lastNameChar;

                                holder.userNameFirstAndLastLetter.setText(fullNameChar);
                            } else {
                                holder.userNameFirstAndLastLetter.setText(firstNameChar);
                            }
                        }

                        break;
                    } else {
                        String getUsername = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                        holder.userName.setText(getUsername);

                        if (snapshot.hasChild("image")){
                            String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                            Picasso.get().load(image).into(holder.userProfile);
                        } else {
                            String[] nameParts = getUsername.split(" ");
                            String firstName = nameParts[0];
                            String firstNameChar = firstName.substring(0, 1).toUpperCase();

                            if (nameParts.length > 1){
                                String lastName = nameParts[nameParts.length - 1];
                                String lastNameChar = lastName.substring(0, 1).toUpperCase();

                                String fullNameChar = firstNameChar + lastNameChar;

                                holder.userNameFirstAndLastLetter.setText(fullNameChar);
                            } else {
                                holder.userNameFirstAndLastLetter.setText(firstNameChar);
                            }
                        }
                    }
                }
                cursor.close();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        messageRef.child(userId).child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat newMessage = dataSnapshot.getValue(Chat.class);
                    assert newMessage != null;
                    holder.newMessage.setText(newMessage.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public Chat getItem(int position){
        return list.get(position);
    }




    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView userProfile;
        TextView userNameFirstAndLastLetter, userName, newMessage;

        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;

            userProfile = itemView.findViewById(R.id.userProfile);
            userNameFirstAndLastLetter = itemView.findViewById(R.id.userNameFirstAndLastLetter);
            userName = itemView.findViewById(R.id.userName);
            newMessage = itemView.findViewById(R.id.newMessage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            v.setPressed(true);
            onItemClickListener.OnClickItem(getAdapterPosition());
        }

        public interface OnItemClickListener{
            void OnClickItem(int position);
        }
    }
}
