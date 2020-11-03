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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.List;

public class VerticalScrollableContactListsAdapter extends RecyclerView.Adapter<VerticalScrollableContactListsAdapter.ViewHolder>{
    DatabaseReference userRef;

    List<Contacts> lists;
    private OnItemClickListener onItemClickListener;


    public VerticalScrollableContactListsAdapter(List<Contacts> lists, OnItemClickListener onItemClickListener){
        this.lists = lists;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating item layout design for contact list
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout, parent, false);

        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Contacts getPosition = lists.get(position);

//        holder.contactNameFirstLetter.setText(getPosition.getContact_name_first_letter());
        Picasso.get().load(getPosition.getImage()).placeholder(R.drawable.blank_profile_picture).into(holder.contactProfile);
        holder.contactUserName.setText(getPosition.getContact_name());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView contactNameFirstLetter;
        ImageView contactProfile;
        TextView contactUserName;
        CardView inviteCard;

        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            this.onItemClickListener = onItemClickListener;

            contactNameFirstLetter = itemView.findViewById(R.id.contactNameFirstLetter);
            contactProfile = itemView.findViewById(R.id.contactProfile);
            contactUserName = itemView.findViewById(R.id.contactUserName);
            inviteCard = itemView.findViewById(R.id.inviteCard);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.OnClickItem(getAdapterPosition());
            v.setPressed(true);
        }
    }


    public interface OnItemClickListener{
        void OnClickItem(int position);
    }

    public void isUserAvailable(){
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }
}
