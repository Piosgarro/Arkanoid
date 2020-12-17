package com.example.android.arkanoid;

import android.os.Handler;

public class UpdateThread extends Thread {

    Handler updateHandler;
    public boolean threadSuspended;

    public UpdateThread(Handler uh) {
        super();
        updateHandler = uh;
    }

    public void run() {
        while (!threadSuspended) {
            try {
                sleep(32);
            } catch (Exception ignored) {
            }
            updateHandler.sendEmptyMessage(0);
        }
    }
}
