package com.chatapp.application.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.chatapp.application.activity.OTPActivity;
import com.chatapp.application.R;
import com.chatapp.application.activity.RegisterActivity;

import java.util.Objects;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    private static final int REQUEST_CODE = 1;

    TextView phoneNumberText, dialogEdit, dialogYes;

    String getFullPhoneNumber, getPhoneNumber;

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
        getFullPhoneNumber = this.getArguments().getString("fullPhoneNumber");
        getPhoneNumber = this.getArguments().getString("phoneNumber");

        //setting the text i.e phone number to dialog fragment
        phoneNumberText.setText(getFullPhoneNumber);


        dialogEdit = view.findViewById(R.id.dialogEdit);
        dialogEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });


        dialogYes = view.findViewById(R.id.dialogYes);
        dialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestPermission();

            }
        });
    }

    //get contact permission
    public void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, REQUEST_CODE);

        } else {
            startIntent();
        }
    }


    //get the result of permissions on run time
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                onClickAllow();
            }else {
                onClickDeny();
            }
        }
    }


    public void startIntent(){
        Intent intent = new Intent(getContext(), OTPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("fullPhoneNumber", getFullPhoneNumber);
        intent.putExtra("phoneNumber", getPhoneNumber);

        startActivity(intent);
    }

    private void onClickAllow(){
        startIntent();
    }

    private void onClickDeny(){
        startIntent();
    }


    @Override
    public void onStart() {
        super.onStart();

        Objects.requireNonNull(getDialog()).getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
