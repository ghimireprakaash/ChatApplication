package com.chatapp.application.customdialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import com.chatapp.application.R;

public class CustomLoadingDialog {
    Activity activity;
    AlertDialog dialog;


    public CustomLoadingDialog(Activity myActivity){
        activity = myActivity;
    }

    @SuppressLint("InflateParams")
    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_loading_dialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
