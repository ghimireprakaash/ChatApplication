package com.chatapp.application.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context context;
    private final List<Chat> chat;


    FirebaseUser firebaseUser;
    DatabaseReference chatRef;

    String userId;


    public MessageAdapter(Context context, List<Chat> chat, String userId) {
        this.context = context;
        this.chat = chat;
        this.userId = userId;
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
        final String senderId = model.getSender();
        final String receiverId = model.getReceiver();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chatRef = FirebaseDatabase.getInstance().getReference();
        chatRef.child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (senderId.equals(firebaseUser.getUid()) && receiverId.equals(userId)
                            || senderId.equals(userId) && receiverId.equals(firebaseUser.getUid())){
                        String messageSentTime = model.getMessageSentTime();

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat _24HourTimeFormat = new SimpleDateFormat("HH:mm");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat _12HourTimeFormat = new SimpleDateFormat("hh:mm a");
                        try {
                            Date _12HourTime = _12HourTimeFormat.parse(messageSentTime);
                            if (DateFormat.is24HourFormat(context)){
                                assert _12HourTime != null;
                                holder.messageSentTime.setText(_24HourTimeFormat.format(_12HourTime));
                            } else {
                                holder.messageSentTime.setText(messageSentTime);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        chatRef.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image"))){
                    String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();

                    Picasso.get().load(image).placeholder(R.drawable.blank_profile_picture).into(holder.profile_image);
                }
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
        ImageView profile_image;
        TextView messageSentTime;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            messageSentTime = itemView.findViewById(R.id.messageSentTime);
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
