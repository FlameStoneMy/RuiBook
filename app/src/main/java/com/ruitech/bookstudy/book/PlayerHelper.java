package com.ruitech.bookstudy.book;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ruitech.bookstudy.RuiPreferenceUtil;

import java.io.IOException;

import androidx.annotation.CallSuper;

public class PlayerHelper {
    private static final String TAG = "PlayerHelper";

    private HandlerThread workerThread;
    private Handler workerHandler;
    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STATE_CHANGED:
                    Log.d(TAG, "mainHandler handleMessage: " + msg.what + " " + msg.arg1);
                    callback.onStateChanged(msg.arg1);
                    break;
            }
        }
    };

    private static final int MSG_STATE_CHANGED = 1;

    private static final int MSG_PLAY = 1;
    private static final int MSG_PAUSE = 2;
    private static final int MSG_RESUME = 3;
//    private static final int MSG_TOGGLE = 4;
    private static final int MSG_STOP = 5;
    private static final int MSG_CHANGE_SPEED = 6;
    private Callback callback;
    public PlayerHelper(Callback callback) {
        this.callback = callback;
        workerThread = new HandlerThread("RuiPlayerWorker");
        workerThread.start();
        workerHandler = new WorkerHandler(workerThread.getLooper());
    }

    public static final int STATE_INIT = 0;
//    public static final int STATE_PREPARED = 1;
    public static final int STATE_STARTED = 2;
    public static final int STATE_PAUSED = 3;
    public static final int STATE_ERROR = 4;
    public static final int STATE_COMPLETION = 5;
    public interface Callback {
        void onStateChanged(int state);
    }

    public void playOnce(Context context, String bookId, String bookGenuineId, int pageNum, int musicNum) {
        Log.d(TAG, "playOnce: " + musicNum);
        Message.obtain(workerHandler, MSG_PLAY,
                SinglePlayTask.singleTask(context, bookId, bookGenuineId, pageNum, musicNum, mainHandler))
                .sendToTarget();
    }

    public void playRepeatedly(Context context, String bookId, String bookGenuineId, int pageNum, int musicNum) {
        Log.d(TAG, "playRepeatedly: " + musicNum);
        Message.obtain(workerHandler, MSG_PLAY,
                SinglePlayTask.singleLoopTask(context, bookId, bookGenuineId, pageNum, musicNum, mainHandler))
                .sendToTarget();
    }

    public void playRepeatedly(Context context, String bookId, String bookGenuineId, int pageNum, int musicStartNum, int musicEndNum) {
        Log.d(TAG, "playRepeatedly: " + musicStartNum + " " + musicEndNum);
        Message.obtain(workerHandler, MSG_PLAY,
                MultiPlayTask.multiLoopTask(context, bookId, bookGenuineId, pageNum, musicStartNum, musicEndNum, mainHandler))
                .sendToTarget();
    }

    public void pause() {
        Message.obtain(workerHandler, MSG_PAUSE).sendToTarget();
    }

    public void resume() {
        Message.obtain(workerHandler, MSG_RESUME).sendToTarget();
    }

    public void stop() {
        Message.obtain(workerHandler, MSG_STOP).sendToTarget();
    }

    public void changeSpeed() {
        Message.obtain(workerHandler, MSG_CHANGE_SPEED).sendToTarget();
    }

    private static abstract class PlayTask implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        protected Context context;
        protected String bookId;
        protected String bookGenuineId;
        protected int pageNum;
        protected boolean loop;
        protected MediaPlayer player;
        protected Handler eventHandler;

        protected int playerState = STATE_INIT;

        PlayTask(Context context, String bookId, String bookGenuineId, int pageNum, boolean loop, Handler eventHandler) {
            this.context = context;
            this.bookId = bookId;
            this.bookGenuineId = bookGenuineId;
            this.pageNum = pageNum;
            this.loop = loop;
            this.eventHandler = eventHandler;
        }

        protected void dispatchPlayerState(int playerState) {
            this.playerState = playerState;
            Message.obtain(eventHandler, MSG_STATE_CHANGED, playerState, 0).sendToTarget();
        }

        protected void playInternal(int musicNum) {
            android.util.Log.d(TAG, "playInternal: " + player + " " + musicNum + " " + RuiPreferenceUtil.getClickReadSpeed());
            try {
                player = newPlayer();
                player.setDataSource(context, BookUtil.getMusicUri(bookId, bookGenuineId, pageNum, musicNum));
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                PlaybackParams params = new PlaybackParams();
                params.setSpeed(RuiPreferenceUtil.getClickReadSpeed());
                player.setPlaybackParams(params);
            }
            player.start();
            dispatchPlayerState(STATE_STARTED);
        }

        protected MediaPlayer newPlayer() {
            MediaPlayer player = new MediaPlayer();
            player.setOnErrorListener(this);
            return player;
        }

        public abstract void play();

        public void pause() {
            Log.d(TAG, "pause: " + isPlaying() + " " + Thread.currentThread().getId());
            if (playerState == STATE_STARTED) {
                player.pause();
                dispatchPlayerState(STATE_PAUSED);
            }
        }

        public void resume() {
            Log.d(TAG, "resume: " + isPlaying() + " " + Thread.currentThread().getId());
            if (playerState == STATE_PAUSED) {
                player.start();
                dispatchPlayerState(STATE_STARTED);
            }
        }

        public void stop() {
            player.release();
            player = null;
        }

        public boolean isPlaying() {
            switch (playerState) {
                case STATE_STARTED:
                case STATE_PAUSED:
                    return player.isPlaying();
                default:
                    return false;
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletion: ");
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "onError: ");
            dispatchPlayerState(STATE_ERROR);
            return false;
        }
    }

    private static class SinglePlayTask extends PlayTask {
        private int musicNum;

        SinglePlayTask(Context context, String bookId, String bookGenuineId, int pageNum, boolean loop, Handler eventHandler) {
            super(context, bookId, bookGenuineId, pageNum, loop, eventHandler);
        }

        @Override
        protected MediaPlayer newPlayer() {
            MediaPlayer player = super.newPlayer();
            if (loop) {
                player.setLooping(true);
            } else {
                player.setOnCompletionListener(this);
            }
            Log.d(TAG, "new player: " + Thread.currentThread().getId());
            return player;
        }

        @Override
        public void play() {
            playInternal(musicNum);
        }

        public static PlayTask singleTask(Context context, String bookId, String bookGenuineId, int pageNum, int musicNum, Handler eventHandler) {
            SinglePlayTask playTask = new SinglePlayTask(context, bookId, bookGenuineId, pageNum, false, eventHandler);
            playTask.musicNum = musicNum;
            return playTask;
        }

        public static PlayTask singleLoopTask(Context context, String bookId, String bookGenuineId, int pageNum, int musicNum, Handler eventHandler) {
            SinglePlayTask playTask = new SinglePlayTask(context, bookId, bookGenuineId, pageNum, true, eventHandler);
            playTask.musicNum = musicNum;
            return playTask;
        }
    }

    private static class MultiPlayTask extends PlayTask {

        private int musicStartNum;
        private int musicEndNum;

        private int currMusicNum;

        MultiPlayTask(Context context, String bookId, String bookGenuineId, int pageNum, boolean loop, Handler eventHandler) {
            super(context, bookId, bookGenuineId, pageNum, loop, eventHandler);
        }

        @Override
        protected MediaPlayer newPlayer() {
            MediaPlayer player = super.newPlayer();
            player.setOnCompletionListener(this);
            return player;
        }

        @Override
        public void play() {
            currMusicNum = musicStartNum;
            Log.d(TAG, "play: " + Thread.currentThread().getId());
            playInternal(currMusicNum);
        }

        @Override
        public void resume() {
            Log.d(TAG, "resume: " + isPlaying() + " " + Thread.currentThread().getId());
            if (playerState == STATE_COMPLETION) {
                currMusicNum = getNextNum();
                playInternal(currMusicNum);
            } else {
                super.resume();
            }
        }

        private int getNextNum() {
            return (currMusicNum + 1) > musicEndNum ? musicStartNum : (currMusicNum + 1);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletion: " + Thread.currentThread().getId() + " " + playerState);
            mp.release();
            if (playerState == STATE_STARTED) {
                currMusicNum = getNextNum();
                playInternal(currMusicNum);
            } else {
                playerState = STATE_COMPLETION;
            }
        }

        public static PlayTask multiLoopTask(Context context, String bookId, String bookGenuineId, int pageNum, int musicStartNum, int musicEndNum, Handler eventHandler) {
            MultiPlayTask playTask = new MultiPlayTask(context, bookId, bookGenuineId, pageNum, true, eventHandler);
            playTask.musicStartNum = musicStartNum;
            playTask.musicEndNum = musicEndNum;
            return playTask;
        }
    }

    private class WorkerHandler extends Handler {
        private PlayTask currPlayTask;

        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "WorkerHandler handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_PLAY:
                    if (currPlayTask != null) {
                        currPlayTask.stop();
                    }
                    currPlayTask = (PlayTask) msg.obj;
                    currPlayTask.play();
                    break;
                case MSG_PAUSE:
                    if (currPlayTask != null) {
                        currPlayTask.pause();
                    }
                    break;
                case MSG_RESUME:
                    if (currPlayTask != null) {
                        currPlayTask.resume();
                    }
                    break;
//                case MSG_TOGGLE:
//                    if (currPlayTask != null) {
//                        if (currPlayTask.isPlaying()) {
//                            currPlayTask.pause();
//                        } else {
//                            currPlayTask.resume();
//                        }
//                    }
//                    break;
                case MSG_STOP:
                    if (currPlayTask != null) {
                        currPlayTask.stop();
                        currPlayTask = null;
                    }
                    break;
                case MSG_CHANGE_SPEED:
                    if (currPlayTask != null) {
                        switch (currPlayTask.playerState) {
                            case STATE_STARTED:
                            case STATE_PAUSED:
                                MediaPlayer player = currPlayTask.player;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    player.setPlaybackParams(
                                            player.getPlaybackParams().setSpeed(RuiPreferenceUtil.getClickReadSpeed()));
                                }
                                break;
                        }
                    }
            }
        }
    }
}
