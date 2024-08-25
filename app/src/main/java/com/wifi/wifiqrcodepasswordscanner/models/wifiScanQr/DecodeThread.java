package com.wifi.wifiqrcodepasswordscanner.models.wifiScanQr;


import android.os.Handler;
import android.os.Looper;


import com.wifi.wifiqrcodepasswordscanner.ui.activities.ScanWifiQrActivity;

import java.util.concurrent.CountDownLatch;


/**
 * This thread does all the heavy lifting of decoding the images.
 */
final class DecodeThread extends Thread {

    private final ScanWifiQrActivity mActivity;
    private Handler mHandler;
    private final CountDownLatch mHandlerInitLatch;

    DecodeThread(ScanWifiQrActivity activity) {
        this.mActivity = activity;
        mHandlerInitLatch = new CountDownLatch(1);
    }

    Handler getHandler() {
        try {
            mHandlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return mHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new DecodeHandler(mActivity);
        mHandlerInitLatch.countDown();
        Looper.loop();
    }

}
