package com.wifi.wifiqrcodepasswordscanner.models.wifiScanQr;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.blikoon.qrcodescanner.camera.CameraManager;
import com.google.zxing.Result;
import com.wifi.wifiqrcodepasswordscanner.ui.activities.ScanWifiQrActivity;

public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getName();

    private final ScanWifiQrActivity mActivity;
    private final DecodeThread mDecodeThread;
    private State mState;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    public CaptureActivityHandler(ScanWifiQrActivity activity) {
        this.mActivity = activity;
        mDecodeThread = new DecodeThread(activity);
        mDecodeThread.start();
        mState = State.SUCCESS;


        // Start ourselves capturing previews and decoding.
        try {
            restartPreviewAndDecode();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(Message message) {

        if (message.what == com.blikoon.qrcodescanner.R.id.auto_focus) {
            Log.d(TAG, "Got auto-focus message");
            // When one auto focus pass finishes, start another. This is the closest thing to
            // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
            if (mState == State.PREVIEW) {
                try {
                    CameraManager.get().requestAutoFocus(this, com.blikoon.qrcodescanner.R.id.auto_focus);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } else if (message.what == com.blikoon.qrcodescanner.R.id.decode_succeeded) {
            Log.e(TAG, "Got decode succeeded message");
            mState = State.SUCCESS;
            mActivity.handleDecode((Result) message.obj);

        } else if (message.what == com.blikoon.qrcodescanner.R.id.decode_failed) {
            // We're decoding as fast as possible, so when one decode fails, start another.
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), com.blikoon.qrcodescanner.R.id.decode);
        }
    }

    public void quitSynchronously() {
        try {
            mState = State.DONE;
            CameraManager.get().stopPreview();
            Message quit = Message.obtain(mDecodeThread.getHandler(), com.blikoon.qrcodescanner.R.id.quit);
            quit.sendToTarget();
            try {
                mDecodeThread.join();
            } catch (InterruptedException e) {
                // continue
            }

            // Be absolutely sure we don't send any queued up messages
            removeMessages(com.blikoon.qrcodescanner.R.id.decode_succeeded);
            removeMessages(com.blikoon.qrcodescanner.R.id.decode_failed);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void restartPreviewAndDecode() {
        try {
            if (mState != State.PREVIEW) {
                CameraManager.get().startPreview();
                mState = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), com.blikoon.qrcodescanner.R.id.decode);
                CameraManager.get().requestAutoFocus(this, com.blikoon.qrcodescanner.R.id.auto_focus);

            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

}
