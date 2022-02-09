package com.chatapp.application.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.model.Contacts;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultipleContactSelectAdapter extends RecyclerView.Adapter<MultipleContactSelectAdapter.ViewHolder>{
    Context context;
    List<Contacts> friendsContactList;
    String userId;

    List<Contacts> selectedContacts, removeSelectedContacts;


    public MultipleContactSelectAdapter(Context context, List<Contacts> friendsContactList, String userId) {
        this.context = context;
        this.friendsContactList = friendsContactList;
        this.userId = userId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Contacts getPosition = friendsContactList.get(position);
        final String name = getPosition.getContact_name();
        holder.contactName.setText(name);

        DatabaseReference friendsGroupRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendsGroupRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && (snapshot.hasChild("image"))){
                    String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();

                    Picasso.get().load(image).into(holder.userProfile);
                } else {
                    if (name.matches("\\w")) {
                        String[] nameParts = name.split(" ");
                        String firstName = nameParts[0];
                        String firstNameChar = firstName.substring(0, 1).toUpperCase();

                        if (nameParts.length > 1) {
                            String lastName = nameParts[nameParts.length - 1];
                            String lastNameChar = lastName.substring(0, 1).toUpperCase();

                            String fullNameChar = firstNameChar + lastNameChar;

                            holder.userNameFirstAndLastLetter.setText(fullNameChar);
                        } else {
                            holder.userNameFirstAndLastLetter.setText(firstNameChar);
                        }
                    } else {
                        Picasso.get().load(R.drawable.blank_profile_picture).into(holder.userProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.contactMaterialCardLayout.setOnClickListener(v -> {
            getPosition.setSelected(!getPosition.isSelected());
            if (getPosition.isSelected()){
                holder.contactSelected.setVisibility(View.VISIBLE);
                addSelectedContacts();
            } else {
                holder.contactSelected.setVisibility(View.INVISIBLE);
                removeSelectedContacts();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsContactList.size();
    }


    public void addSelectedContacts(){
        selectedContacts = new ArrayList<>();
        for (Contacts contacts : friendsContactList){
            if (contacts.isSelected()){
                selectedContacts.add(contacts);
            }
        }
    }

    public void removeSelectedContacts(){
        removeSelectedContacts = new ArrayList<>();
        for (Contacts contacts : selectedContacts){
            if (contacts.isSelected()){
                removeSelectedContacts.remove(contacts);
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView userProfile, contactSelected;
        TextView userNameFirstAndLastLetter, contactName;
        MaterialCardView contactMaterialCardLayout, inviteCard;


        public ViewHolder(View itemView){
            super(itemView);

            contactMaterialCardLayout = itemView.findViewById(R.id.contactMaterialCardLayout);
            userProfile = itemView.findViewById(R.id.contactProfile);
            contactSelected = itemView.findViewById(R.id.contactSelected);
            userNameFirstAndLastLetter = itemView.findViewById(R.id.contactNameFirstAndLastLetter);
            contactName = itemView.findViewById(R.id.contactUserName);
            inviteCard = itemView.findViewById(R.id.inviteCard);
            inviteCard.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            v.setPressed(true);
        }
    }
}
