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
import com.chatapp.application.model.Contacts;
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

public class ShowSearchResultAdapter extends RecyclerView.Adapter<ShowSearchResultAdapter.ViewHolder> {
    Context context;
    List<Contacts> userList;

    DatabaseReference userRef;
    FirebaseUser firebaseUser;

    private final ViewHolder.OnItemClickListener onItemClickListener;

    public ShowSearchResultAdapter(Context context, List<Contacts> userList, ViewHolder.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.userList = userList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout, parent, false);

        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Contacts getPosition = userList.get(position);

        holder.userName.setText(getPosition.getContact_name());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (!user.getUid().equals(firebaseUser.getUid())){
                        Picasso.get().load(user.getImage()).into(holder.userProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        holder.userNameFirstAndLastLetter.setText(getPosition.getUserName_firstLetter_and_lastLetter());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public Contacts getItem(int position) {
        return userList.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userProfile;
        TextView userNameFirstAndLastLetter, userName, inviteText;

        OnItemClickListener onItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener){
            super(itemView);

            this.onItemClickListener = onItemClickListener;

            userProfile = itemView.findViewById(R.id.contactProfile);
            userNameFirstAndLastLetter = itemView.findViewById(R.id.contactNameFirstAndLastLetter);
            userName = itemView.findViewById(R.id.contactUserName);
            inviteText = itemView.findViewById(R.id.inviteText);
            inviteText.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.OnClickItem(getAdapterPosition());
            }
        }

        public interface OnItemClickListener{
            void OnClickItem(int position);
        }
    }
}
