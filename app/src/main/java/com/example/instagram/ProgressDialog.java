package com.example.instagram;

import android.app.AlertDialog;
import android.app.Dialog;

public class ProgressDialog {

    public void setDialog(AlertDialog.Builder builder , String message, boolean show){
        builder.setMessage(message);
        builder.setView(R.layout.progress);
        Dialog dialog = builder.create();
        if (show)dialog.show();
        else dialog.dismiss();
    }
    public void setDialog(AlertDialog.Builder builder , boolean show){
        builder.setView(R.layout.progress);
        Dialog dialog = builder.create();
        if (show)dialog.show();
        else dialog.dismiss();
    }
}
/*
XML : progress.xml
        final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        final ProgressDialog pd = new ProgressDialog();
        pd.setDialog(builder,true); or  pd.setDialog(builder,true);
 */
