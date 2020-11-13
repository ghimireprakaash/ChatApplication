package com.chatapp.application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private FirebaseUser firebaseUser;
    private DatabaseReference usersRef;
    private String getUserId;

    private List<User> list;
    private ChatListAdapter.ViewHolder.OnItemClickListener onItemClickListener;


    public ChatListAdapter(List<User> list, ViewHolder.OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_layout, parent, false);
        return new ChatListAdapter.ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.ViewHolder holder, int position) {
        final User model = list.get(position);

        holder.userName.setText(model.getUsername());

        getUserId = model.getUid();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRef.child(getUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image")) && (snapshot.hasChild("username")) && (!getUserId.equals(firebaseUser.getUid()))){

                    Picasso.get().load(model.getImage()).into(holder.userProfile);

                } else {
                    String getUsername = model.getUsername();
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public User getItem(int position) {
        return list.get(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView userProfile;
        TextView userNameFirstAndLastLetter, userName;

        ChatListAdapter.ViewHolder.OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, ChatListAdapter.ViewHolder.OnItemClickListener onItemClickListener) {
            super(itemView);

            this.onItemClickListener = onItemClickListener;

            userProfile = itemView.findViewById(R.id.userProfile);
            userNameFirstAndLastLetter = itemView.findViewById(R.id.userNameFirstAndLastLetter);
            userName = itemView.findViewById(R.id.userName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null){
                onItemClickListener.OnItemClick(getAdapterPosition());
            }
        }


        public interface OnItemClickListener{
            void OnItemClick(int position);
        }
    }
}
