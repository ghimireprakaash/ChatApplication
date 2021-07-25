package com.chatapp.application.fragments;

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
import java.util.Objects;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    TextView phoneNumberText, dialogEdit, dialogYes;

    String fullPhoneNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.dialog_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumberText = view.findViewById(R.id.phoneNumberText);

        assert this.getArguments() != null;
        fullPhoneNumber = this.getArguments().getString("fullPhoneNumber");

        //setting the text i.e phone number to dialog fragment
        phoneNumberText.setText(fullPhoneNumber);


        dialogEdit = view.findViewById(R.id.dialogEdit);
        dialogEdit.setOnClickListener(view12 -> Objects.requireNonNull(getDialog()).dismiss());


        dialogYes = view.findViewById(R.id.dialogYes);
        dialogYes.setOnClickListener(view1 -> startIntent());
    }

    public void startIntent(){
        Intent intent = new Intent(getContext(), OTPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("fullPhoneNumber", fullPhoneNumber);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();

        Objects.requireNonNull(getDialog()).getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
