package com.chatapp.application.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.model.ContactLists;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class VerticalScrollableContactListsRecyclerView extends RecyclerView.Adapter<VerticalScrollableContactListsRecyclerView.ViewHolder>{
    Context context;
    List<ContactLists> lists;


    public VerticalScrollableContactListsRecyclerView(Context context, List<ContactLists> lists){
        this.context = context;
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
        ContactLists getPosition = lists.get(position);

        holder.contactUserImage.setImageResource(getPosition.getContact_user_image());
        holder.contactUserName.setText(getPosition.getContact_name());
        holder.contactNumber.setText(getPosition.getContact_number());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView contactUserImage;
        TextView contactUserName, contactNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactUserImage = itemView.findViewById(R.id.contactUserImage);
            contactUserName = itemView.findViewById(R.id.contactUserName);
            contactNumber = itemView.findViewById(R.id.contactNumber);
        }
    }
}
