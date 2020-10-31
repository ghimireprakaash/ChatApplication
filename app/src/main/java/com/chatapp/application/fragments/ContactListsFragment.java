package com.chatapp.application.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.activity.VisitContactProfile;
import com.chatapp.application.adapter.VerticalScrollableContactListsAdapter;
import com.chatapp.application.model.Contacts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactListsFragment extends Fragment implements VerticalScrollableContactListsAdapter.OnItemClickListener {
    Context context;

    RecyclerView recyclerView;
    List<Contacts> list;
    VerticalScrollableContactListsAdapter adapter;

    DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        View view = inflater.inflate(R.layout.fragment_contact_lists, container, false);


        //Initializing recyclerView with id of RecyclerView of its xml
        recyclerView = view.findViewById(R.id.contactRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // checkPermission if permission was not granted
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){
            list = getContacts();
        }else {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        }

        adapter = new VerticalScrollableContactListsAdapter(list, this);
        recyclerView.setAdapter(adapter);


        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                getContacts();
            }
        }
    }

    private List<Contacts> getContacts() {
        List<Contacts> contactsList = new ArrayList<>();
        Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        assert cursor != null;
        while (cursor.moveToNext()){
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Contacts contactInfo = new Contacts();
            contactInfo.setContact_name(contactName);
            contactInfo.setContact_number(contactNumber);

            //Now attaching contactInfo to ArrayList object
            contactsList.add(contactInfo);
        }
        cursor.close();

        return contactsList;
    }


    private void retrieveUserImage(){
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image")) && (snapshot.hasChild("username"))){
                    String userImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                    String userName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("Alert Message!!!");
        builder.setMessage("Currently doesn't support full feature since the maintenance process is being held.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    @Override
    public void OnClickItem(int position) {
        Intent intent = new Intent(getContext(), VisitContactProfile.class);
        startActivity(intent);
    }
}