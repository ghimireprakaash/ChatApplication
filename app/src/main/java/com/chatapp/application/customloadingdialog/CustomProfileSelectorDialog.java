package com.chatapp.application.customloadingdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CustomProfileSelectorDialog {
    Context mContext;
    Activity mActivity;

    //Creating constructor
    public CustomProfileSelectorDialog(Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }


    //creating dialog
    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Edit Profile Photo");
        builder.setCancelable(true);
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }
}
