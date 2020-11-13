package com.chatapp.application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.model.Contacts;
import com.squareup.picasso.Picasso;
import java.util.List;

public class VerticalScrollableContactListsAdapter extends RecyclerView.Adapter<VerticalScrollableContactListsAdapter.ViewHolder>{
    List<Contacts> lists;
    private final OnItemClickListener onItemClickListener;


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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Contacts getPosition = lists.get(position);

        holder.contactNameFirstAndLastLetter.setText(getPosition.getUserName_firstLetter_and_lastLetter());
        Picasso.get().load(getPosition.getImage()).into(holder.contactProfile);
        holder.contactUserName.setText(getPosition.getContact_name());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView contactNameFirstAndLastLetter;
        ImageView contactProfile;
        TextView contactUserName, inviteText;

        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            this.onItemClickListener = onItemClickListener;

            contactNameFirstAndLastLetter = itemView.findViewById(R.id.contactNameFirstAndLastLetter);
            contactProfile = itemView.findViewById(R.id.contactProfile);
            contactUserName = itemView.findViewById(R.id.contactUserName);
            inviteText = itemView.findViewById(R.id.inviteText);


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
}
