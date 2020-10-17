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
import java.util.Objects;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    TextView phoneNumberText, dialogEdit, dialogYes;

    String getFullPhoneNumber;

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


        getFullPhoneNumber = this.getArguments().getString("phNumberWithCountryCode");


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

        return view;
    }




    //get contact permission
    public void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, 1);

        } else {
            onClickYes();
        }
    }


    //get the result of permissions on run time
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                onClickYes();
            }else {
                onClickYes();
            }
        }
    }



    public void onClickYes(){
        Objects.requireNonNull(getDialog()).setCancelable(false);
        getDialog().dismiss();

        Intent intent = new Intent(getContext(), OTPActivity.class);
        intent.putExtra("getFullPhoneNumber", getFullPhoneNumber);

        startActivity(intent);
    }
}
