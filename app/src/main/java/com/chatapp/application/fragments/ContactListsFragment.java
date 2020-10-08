package com.chatapp.application.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chatapp.application.R;
import com.chatapp.application.adapter.VerticalScrollableContactListsRecyclerView;
import com.chatapp.application.model.ContactLists;
import java.util.List;
import java.util.Objects;

public class ContactListsFragment extends Fragment {
    RecyclerView recyclerView;
    FragmentManager fragmentManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        View view = inflater.inflate(R.layout.fragment_contact_lists, container, false);


        //Initializing recyclerView with id of RecyclerView of its xml
        recyclerView = view.findViewById(R.id.contactRecyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);



        return view;
    }



    public void getContacts(){
        Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);

        assert cursor != null;
        while (cursor.moveToNext()){

            //Now adding it with lists


            //Now attaching contactsInfo with List
        }
        cursor.close();
    }
}