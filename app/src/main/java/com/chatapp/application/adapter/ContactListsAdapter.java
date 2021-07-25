package com.chatapp.application.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.activity.ShowFriendsActivity;
import com.chatapp.application.model.Contacts;
import com.chatapp.application.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Objects;

public class ContactListsAdapter extends RecyclerView.Adapter<ContactListsAdapter.ViewHolder>{
    private static final String TAG = "Adapter";

    List<Contacts> lists;
    private final OnItemClickListener onItemClickListener;

    DatabaseReference userRef;


    public ContactListsAdapter(List<Contacts> lists, OnItemClickListener onItemClickListener){
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

        holder.contactUserName.setText(getPosition.getContact_name());
        holder.contactNameFirstAndLastLetter.setText(getPosition.getUserName_firstLetter_and_lastLetter());


        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    String userId = user.getUid();
                    if (!userId.equals(currentUser)){
                        String contact = user.getContact();
                        String phoneNumber = getPosition.getContact_number().replaceAll("\\s|-", "");

                        ShowFriendsActivity showFriendsActivity = new ShowFriendsActivity();
                        if (contact.equals(phoneNumber) || showFriendsActivity.getPhoneNumberWithoutCountryCode(contact).equals(phoneNumber)){
                            String image = user.getImage();
                            Picasso.get().load(image).placeholder(R.drawable.blank_profile_picture).into(holder.contactProfile);
                            holder.contactNameFirstAndLastLetter.setVisibility(View.GONE);
                        }
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
        return lists.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }





    //ViewHolder Class...
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView contactNameFirstAndLastLetter;
        ImageView contactProfile;
        TextView contactUserName;
        MaterialCardView contactMaterialCardLayout, inviteCard;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            this.onItemClickListener = onItemClickListener;

            contactNameFirstAndLastLetter = itemView.findViewById(R.id.contactNameFirstAndLastLetter);
            contactProfile = itemView.findViewById(R.id.contactProfile);
            contactUserName = itemView.findViewById(R.id.contactUserName);
            inviteCard = itemView.findViewById(R.id.inviteCard);
            contactMaterialCardLayout = itemView.findViewById(R.id.contactMaterialCardLayout);


            contactMaterialCardLayout.setOnClickListener(this);
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
