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
import com.chatapp.application.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;

public class RetrieveUsersAdapter extends RecyclerView.Adapter<RetrieveUsersAdapter.ViewHolder>{
    Context context;
    private DatabaseReference usersRef;

    private final List<User> list;
    private final ViewHolder.OnItemClickListener onItemClickListener;


    public RetrieveUsersAdapter(Context context, List<User> list, ViewHolder.OnItemClickListener onItemClickListener) {
        this.context = context;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User model = list.get(position);
        String getUserId = model.getUid();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(getUserId);
        String userContactNumber = model.getContact();

        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (cursor.moveToNext()) {
            String phoneContact = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s|-", "");
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            ShowFriendsActivity showFriendsActivity = new ShowFriendsActivity();
            if (userContactNumber.equals(phoneContact) || showFriendsActivity.getPhoneNumberWithoutCountryCode(userContactNumber).equals(phoneContact)){
                //setting username to the placeHolder that we get from the user's phone contact list.s
                holder.userName.setText(contactName);

                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("image"))){

                            Picasso.get().load(model.getImage()).into(holder.userProfile);

                        } else {
                            //after we get contact names of matched users it is checked if it matches the regex
                            //And picking first char from first name and last name it is set to the image holder if image is empty in firebase
                            String[] nameParts = contactName.split(" ");
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        cursor.close();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public User getItem(int position) {
        return list.get(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView userProfile;
        TextView userNameFirstAndLastLetter, userName;
        MaterialCardView inviteCard;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;

            userProfile = itemView.findViewById(R.id.contactProfile);
            userNameFirstAndLastLetter = itemView.findViewById(R.id.contactNameFirstAndLastLetter);
            userName = itemView.findViewById(R.id.contactUserName);
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
