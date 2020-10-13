package com.chatapp.application.fragments;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.adapter.VerticalScrollableContactListsAdapter;
import com.chatapp.application.model.ContactLists;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactListsFragment extends Fragment {
    Context context;

    RecyclerView recyclerView;
    List<ContactLists> list;


    public ContactListsFragment(Context context, List<ContactLists> list) {
        this.context = context;
        this.list = list;
    }

    public ContactListsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        View view = inflater.inflate(R.layout.fragment_contact_lists, container, false);


        //Initializing recyclerView with id of RecyclerView of its xml
        recyclerView = view.findViewById(R.id.contactRecyclerView);
        recyclerView.setHasFixedSize(true);


        list = getContacts();

        VerticalScrollableContactListsAdapter adapter = new VerticalScrollableContactListsAdapter(list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        recyclerView.setAdapter(adapter);


        return view;
    }



    public List<ContactLists> getContacts(){
        context = getContext();

        List<ContactLists> contactLists = new ArrayList<>();

        Cursor cursor = null;
        assert context != null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    null,null,null,null);
        }catch (Exception e){
            Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
        }

        assert cursor != null;
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0){
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);

                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);


                    Bitmap photo = null;
                    if (inputStream != null){
                        photo = BitmapFactory.decodeStream(inputStream);
                    }

                    assert cursorInfo != null;
                    while (cursorInfo.moveToNext()){
                        ContactLists contactInfo = new ContactLists();

//                        contactInfo.contact_ID = id;
                        contactInfo.contact_name = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        contactInfo.contact_number = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactInfo.photo = photo;
                        contactInfo.photoURI = pURI;

                        //Now adding it on lists,
                        //Attaching contactsInfo with List
                        contactLists.add(contactInfo);
                    }
                    cursorInfo.close();
                }
            }
        }
        cursor.close();

        return contactLists;
    }
}