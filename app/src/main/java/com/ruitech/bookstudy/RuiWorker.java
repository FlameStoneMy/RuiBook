package com.ruitech.bookstudy;


import android.os.Handler;
import android.os.Looper;

public abstract class RuiWorker<T> {

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public final void execute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                T t = doInBackground();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(t);
                        dump();
                    }
                });
            }
        }).start();
    }

    protected void onPreExecute() {
    }

    protected abstract T doInBackground();

    protected void onPostExecute(T t) {
    }

    private void dump() {
        mainHandler.removeCallbacksAndMessages(null);
        mainHandler = null;
    }
}
