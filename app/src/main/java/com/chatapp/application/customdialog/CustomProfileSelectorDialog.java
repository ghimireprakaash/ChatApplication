package com.chatapp.application.customdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

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
        builder.setPositiveButton("Gallery", (dialogInterface, i) -> {

        });
    }
}
