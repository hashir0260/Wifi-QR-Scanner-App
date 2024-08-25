package com.wifi.wifiqrcodepasswordscanner.models.wifiScanQr;


import android.content.DialogInterface;

import androidx.fragment.app.Fragment;

/**
 * Simple listener used to exit the app in a few cases.
 *
 */
public final class FinishListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener,
        Runnable {

    private final Fragment mActivityToFinish;

    public FinishListener(Fragment activityToFinish) {
        this.mActivityToFinish = activityToFinish;
    }

    public void onCancel(DialogInterface dialogInterface) {
        run();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        run();
    }

    public void run() {
        mActivityToFinish.getActivity().finish();
    }

}
