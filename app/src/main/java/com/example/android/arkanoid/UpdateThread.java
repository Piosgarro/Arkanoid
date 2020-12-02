package com.example.android.arkanoid;

import android.os.Handler;

public class UpdateThread extends Thread {

    Handler updateHandler;

    public UpdateThread(Handler uh) {
        super();
        updateHandler = uh;
    }

    public void run() {
        while (true) {
            try {
                sleep(32);
            } catch (Exception ex) {
            }
            updateHandler.sendEmptyMessage(0);
        }
    }
}
