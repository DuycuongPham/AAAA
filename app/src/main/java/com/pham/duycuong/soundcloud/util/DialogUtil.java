package com.pham.duycuong.soundcloud.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class DialogUtil {
    private Context mContext;
    private AlertDialog mAlertDialog;

    public DialogUtil(Context context){
        mContext = context;
    }

    public void showProgressDialog(String message) {
        if(mAlertDialog==null){
            mAlertDialog = new AlertDialog.Builder(mContext).setMessage(message).create();
            mAlertDialog.show();
        }
    }

    public void hideProgressDialog() {
        Toast.makeText(mContext, "avv", Toast.LENGTH_SHORT).show();
        if (mAlertDialog!=null) {
            mAlertDialog.dismiss();
        }
    }
}
