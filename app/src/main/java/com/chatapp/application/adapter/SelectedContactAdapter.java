package com.chatapp.application.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.model.Contacts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Objects;

public class SelectedContactAdapter extends RecyclerView.Adapter<SelectedContactAdapter.ViewHolder> {
    Context context;
    List<Contacts> selectedContactsList;
    String userId;


    DatabaseReference friendsRef;


    public SelectedContactAdapter(Context context, List<Contacts> selectedContactsList, String userId) {
        this.context = context;
        this.selectedContactsList = selectedContactsList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_contact_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Contacts getPosition = selectedContactsList.get(position);

        friendsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendsRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && (snapshot.hasChild("image"))){
                    String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();

                    Picasso.get().load(image).into(holder.selectedContactProfile);
                } else {
                    String[] nameParts = getPosition.getContact_name().split(" ");
                    String firstName = nameParts[0];
                    String firstNameChar = firstName.substring(0,1).toUpperCase();

                    if (nameParts.length > 1){
                        String lastName = nameParts[nameParts.length - 1];
                        String lastNameChar = lastName.substring(0,1).toUpperCase();

                        String fullNameChar = firstNameChar + lastNameChar;

                        holder.selectedContactNameFirstAndLastLetter.setText(fullNameChar);
                    } else {
                        holder.selectedContactNameFirstAndLastLetter.setText(firstNameChar);
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
        return selectedContactsList == null ? 0 : selectedContactsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView selectedContactProfile;
        TextView selectedContactNameFirstAndLastLetter;

        public ViewHolder(View itemView){
            super(itemView);

            selectedContactProfile = itemView.findViewById(R.id.selectedContactProfile);
            selectedContactNameFirstAndLastLetter = itemView.findViewById(R.id.selectedContactNameFirstAndLastLetter);
        }
    }
}
