package com.example.maincommerce.services;

import android.app.AlertDialog;
import android.content.Context;

import com.example.maincommerce.R;

public class Dialog {
    Context context;
    private static AlertDialog dialog;
    public Dialog(Context context){
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();;
    }

    public AlertDialog getDialog(){
        return dialog;
    }

    public void showDialog(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

}
