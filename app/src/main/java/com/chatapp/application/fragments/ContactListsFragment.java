package com.chatapp.application.fragments;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.activity.VisitContactProfile;
import com.chatapp.application.adapter.ContactListsAdapter;
import com.chatapp.application.model.Contacts;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactListsFragment extends Fragment implements ContactListsAdapter.OnItemClickListener {
    private static final String TAG = "ContactListFragment";

    RecyclerView recyclerView;
    List<Contacts> contactsList;
    ContactListsAdapter adapter;

    DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        View view = inflater.inflate(R.layout.fragment_contact_lists, container, false);

        //Initializing recyclerView with id of RecyclerView of its xml
        recyclerView = view.findViewById(R.id.contactRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // checkPermission if permission was not granted
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){
            getContacts();
        }else {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        }


        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getContacts();
            }
        }
    }

    private void getContacts() {
        contactsList = new ArrayList<>();
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

            String[] nameParts = contactName.split(" ");
            String firstName = nameParts[0];
            String firstNameChar = firstName.substring(0,1).toUpperCase();

            if (nameParts.length > 1){
                String lastName = nameParts[nameParts.length - 1];
                String lastNameChar = lastName.substring(0,1).toUpperCase();

                Log.d(TAG, "getContacts: " + nameParts[0] + nameParts[1]);
                Log.d(TAG, "getContacts: " + firstNameChar + lastNameChar);

                String firstAndLastNameChar = firstNameChar + lastNameChar;

                contactInfo.setUserName_firstLetter_and_lastLetter(firstAndLastNameChar);

            } else {
                Log.d(TAG, "getContacts: " + nameParts[0]);
                Log.d(TAG, "getContacts: " + firstNameChar);

                contactInfo.setUserName_firstLetter_and_lastLetter(firstNameChar);
            }


            //Now attaching contactInfo to ArrayList object
            contactsList.add(contactInfo);
        }

        adapter = new ContactListsAdapter(contactsList, this);
        recyclerView.setAdapter(adapter);

        cursor.close();
    }


    @Override
    public void OnClickItem(int position) {
        String contactUsername = contactsList.get(position).getContact_name();
        String contactNumber = contactsList.get(position).getContact_number();

        Intent intent = new Intent(getContext(), VisitContactProfile.class);
        intent.putExtra("contact_username", contactUsername);
        intent.putExtra("contact_number", contactNumber);
        startActivity(intent);
    }
}