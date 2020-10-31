package com.chatapp.application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.model.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class RetrieveUsersRecyclerViewAdapter extends FirebaseRecyclerAdapter<Contacts, RetrieveUsersRecyclerViewAdapter.ViewHolder> {
    private ViewHolder.OnItemClickListener onItemClickListener;

    /**
     * Initialize a RecyclerView.Adapter that listens to a Firebase query.
     */
    public RetrieveUsersRecyclerViewAdapter(@NonNull FirebaseRecyclerOptions<Contacts> options, ViewHolder.OnItemClickListener onItemClickListener) {
        super(options);
        this.onItemClickListener = onItemClickListener;
    }

    public RetrieveUsersRecyclerViewAdapter(@NonNull FirebaseRecyclerOptions<Contacts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Contacts model) {
        Picasso.get().load(model.getImage()).into(holder.userProfile);
        holder.userName.setText(model.getUsername());
        holder.userPhoneNumber.setText(model.getContact());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView userProfile;
        TextView userNameFirstAndLastLetter, userName, userPhoneNumber;
        CardView inviteCard;

        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            this.onItemClickListener = onItemClickListener;

            userProfile = itemView.findViewById(R.id.contactProfile);
            userNameFirstAndLastLetter = itemView.findViewById(R.id.contactNameFirstLetter);
            userName = itemView.findViewById(R.id.contactUserName);
            userPhoneNumber = itemView.findViewById(R.id.contactNumber);
            inviteCard = itemView.findViewById(R.id.inviteCard);
            inviteCard.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.OnItemClick(getAdapterPosition());
        }


        public interface OnItemClickListener{
            void OnItemClick(int position);
        }
    }
}
