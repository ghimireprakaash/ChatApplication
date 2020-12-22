package com.chatapp.application.adapter;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    ShowFriendsActivity showFriendsActivity;


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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    final User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    String userId = user.getUid();

                    DatabaseReference userRef;
                    userRef = FirebaseDatabase.getInstance().getReference().child("Users");

                    if (!firebaseUser.getUid().equals(userId)){
                        userRef.child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String contact = user.getContact();
                                String getPhoneContact = getPosition.getContact_number().replaceAll("\\s|-", "");

                                showFriendsActivity = new ShowFriendsActivity();

                                if (contact.equals(getPhoneContact)
                                || showFriendsActivity.getPhoneNumberWithoutCountryCode(contact).equals(getPhoneContact)){
                                    holder.inviteText.setVisibility(View.GONE);

                                    if (snapshot.hasChild("image")){
                                        String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();

                                        Picasso.get().load(image).into(holder.contactProfile);
                                        holder.contactNameFirstAndLastLetter.setVisibility(View.GONE);
                                    } else {
                                        holder.contactNameFirstAndLastLetter.setText(getPosition.getUserName_firstLetter_and_lastLetter());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
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
