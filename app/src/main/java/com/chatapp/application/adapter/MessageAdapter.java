package com.chatapp.application.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.model.Chat;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Chat> chat;


    FirebaseUser firebaseUser;
    DatabaseReference userImageRef;

    String userId;


    public MessageAdapter(Context context, List<Chat> chat) {
        this.context = context;
        this.chat = chat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right_item_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left_item_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Chat model = chat.get(position);

        holder.show_message.setText(model.getMessage());

        final User userModel = new User();
        userId = model.getReceiver();
        userImageRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userImageRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(userModel.getImage()).placeholder(R.drawable.blank_profile_picture).into(holder.sender_profile_image);
//                if ((snapshot.exists()) && (snapshot.hasChild("image"))){
//
//                    Picasso.get().load(userModel.getImage()).into(holder.sender_profile_image);
//
//                } else {
//                    holder.sender_profile_image.setImageResource(R.drawable.blank_profile_picture);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView show_message;
        ImageView sender_profile_image;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            sender_profile_image = itemView.findViewById(R.id.sender_profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
