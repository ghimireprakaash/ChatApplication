package com.chatapp.application.dto;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.chatapp.application.activity.OTPActivity;
import com.chatapp.application.R;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    TextView phoneNumberText, dialogEdit, dialogYes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_fragment, container, false);

        phoneNumberText = view.findViewById(R.id.phoneNumberText);

        assert this.getArguments() != null;
        final String getPhoneNumber = this.getArguments().getString("phNumber");


        //setting the text i.e phone number to dialog fragment
        phoneNumberText.setText(getPhoneNumber);


        final String getFullPhoneNumber = this.getArguments().getString("phNumberWithCountryCode");


        dialogEdit = view.findViewById(R.id.dialogEdit);
        dialogEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });


        dialogYes = view.findViewById(R.id.dialogYes);
        dialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OTPActivity.class);
                intent.putExtra("getFullPhoneNumber", getFullPhoneNumber);

                startActivity(intent);
                getDialog().setCancelable(false);
                getDialog().dismiss();
            }
        });

        return view;
    }
}
