package com.chatapp.application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    TextView phoneNumberText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_fragment, container, false);

        phoneNumberText = view.findViewById(R.id.phoneNumberText);

        assert this.getArguments() != null;
        String getPhoneNumber = this.getArguments().getString("phNumber");

        //setting the text i.e phone number to dialog fragment
        phoneNumberText.setText(getPhoneNumber);

        return view;
    }
}
