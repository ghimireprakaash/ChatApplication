package com.chatapp.application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.model.ContactLists;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VerticalScrollableContactListsAdapter extends RecyclerView.Adapter<VerticalScrollableContactListsAdapter.ViewHolder>{
    List<ContactLists> lists;


    public VerticalScrollableContactListsAdapter(List<ContactLists> lists){
        this.lists = lists;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating item layout design for contact list
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ContactLists getPosition = lists.get(position);

//        holder.contactNameFirstLetter.setText(getPosition.getContact_name_first_letter());
        Picasso.get().load(getPosition.image).into(holder.contactProfile);
        holder.contactUserName.setText(getPosition.contact_name);
        holder.contactNumber.setText(getPosition.contact_number);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView contactNameFirstLetter;
        ImageView contactProfile;
        TextView contactUserName, contactNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactNameFirstLetter = itemView.findViewById(R.id.contactNameFirstLetter);
            contactProfile = itemView.findViewById(R.id.contactProfile);
            contactUserName = itemView.findViewById(R.id.contactUserName);
            contactNumber = itemView.findViewById(R.id.contactNumber);
        }
    }
}
