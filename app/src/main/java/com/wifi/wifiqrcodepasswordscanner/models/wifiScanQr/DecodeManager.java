package com.wifi.wifiqrcodepasswordscanner.models.wifiScanQr;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.blikoon.qrcodescanner.R;


public class DecodeManager {

    public void showPermissionDeniedDialog(Context context) {
//        new AlertDialog.Builder(context).setTitle("Notification")
//                .setMessage("")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
    }

    public void showResultDialog(Activity activity, String resultString, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(activity).setTitle("Notification").setMessage(resultString)
                .setPositiveButton("Confirm", listener).show();
    }

    public void showCouldNotReadQrCodeFromScanner(Context context, final OnRefreshCameraListener listener) {
        new AlertDialog.Builder(context).setTitle("Notification")
                .setMessage("Sorry, could not open scan content")
                .setPositiveButton(R.string.qc_code_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.refresh();
                        }


                    }
                }).show();
    }

    public void showCouldNotReadQrCodeFromPicture(Context context) {
        new AlertDialog.Builder(context).setTitle("Notification")
                .setMessage("Could not read your image.Try another image")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public interface OnRefreshCameraListener {
        void refresh();
    }

}
