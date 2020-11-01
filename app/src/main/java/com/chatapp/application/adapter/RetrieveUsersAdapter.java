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
import com.squareup.picasso.Picasso;
import java.util.List;

public class RetrieveUsersAdapter extends RecyclerView.Adapter<RetrieveUsersAdapter.ViewHolder>{
    private List<Contacts> list;
    private ViewHolder.OnItemClickListener onItemClickListener;

    public RetrieveUsersAdapter(List<Contacts> list, ViewHolder.OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contacts model = list.get(position);

        Picasso.get().load(model.getImage()).into(holder.userProfile);
        holder.userName.setText(model.getUsername());
        holder.userPhoneNumber.setText(model.getContact());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Contacts getItem(int position) {
        return list.get(position);
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
            if (onItemClickListener != null){
                onItemClickListener.OnItemClick(getAdapterPosition());
            }
        }


        public interface OnItemClickListener{
            void OnItemClick(int position);
        }
    }
}
