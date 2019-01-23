package com.forasoft.androidopus;

import android.os.Handler;
import android.os.HandlerThread;

class WorkerThread extends HandlerThread {

    public final static String TAG = "WorkerThread";

    WorkerThread() {
        super(TAG);
    }

    private Handler mWorkerHandler = null;

    void prepareHandler() {
        mWorkerHandler = new Handler(getLooper());
    }

    void postTask(Runnable task) {
        mWorkerHandler.post(task);
    }

    void postDelayTask(long delayInMillis, Runnable task) {
        if (delayInMillis == 0) {
            mWorkerHandler.post(task);
            return;
        }

        mWorkerHandler.postDelayed(task, delayInMillis);
    }
}
