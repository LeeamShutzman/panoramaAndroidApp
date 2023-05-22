package com.example.panorama;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

public class LoadingDialog {

    private Context context;
    private Dialog dialog;

    public LoadingDialog(Context context){
        this.context = context;
    }

    public void startLoadingDialog(){

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.loading_screen);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
