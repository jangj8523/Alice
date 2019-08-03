package com.v1.avatar.v1.Helpers;

import android.os.Handler;
import android.os.HandlerThread;

public class BackgroundThreadHelper {
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;


    private void openBackgroundThread(String id) {
        if (mBackgroundHandler != null) return;
        mBackgroundThread = new HandlerThread(id);
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }



    public void setBackgroundTask (Runnable runnable, String id) {


    }


    public BackgroundThreadHelper(String id) {
        mBackgroundThread = null;
        mBackgroundHandler = null;
        openBackgroundThread(id);
    }

    public Handler getBackgroundHelper() {
        return mBackgroundHandler;
    }

    public boolean isAlive () {
        if (mBackgroundHandler != null && mBackgroundThread != null) return true;
        return false;
    }


    public void closeBackgroundThreadHelper() {
        closeBackgroundThread();
    }


    private void closeBackgroundThread() {
        if (mBackgroundThread== null) {
            return;
        }
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }catch (InterruptedException e) {

        }
    }



}
