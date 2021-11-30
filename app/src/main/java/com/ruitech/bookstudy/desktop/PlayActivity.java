package com.ruitech.bookstudy.desktop;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.ruitech.bookstudy.BaseActivity;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.desktop.bean.Video;
import com.ruitech.bookstudy.utils.DateUtil;

import androidx.annotation.Nullable;

public class PlayActivity extends BaseActivity implements //DialogInterface.OnDismissListener,
        View.OnClickListener { //}, View.OnTouchListener {

    private static final String TAG = "PlayActivity";

    private static final String EXTRA_VIDEO = "video";
    public static void start(Context context, Video video) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
        context.startActivity(intent);
    }

    private SeekBar seekBar;
    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private Video video;
    private TextView progressTv, durationTv;
    private ImageView playPauseImg;
    private View bottomPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.fullScreen(this);

        video = (Video) getIntent().getSerializableExtra(EXTRA_VIDEO);

//        setContentView(R.layout.activity_play);
        bottomPanel = findViewById(R.id.bottom_panel);


        int[] colorArr = new int[] {
                getResources().getColor(R.color._88000000),
                getResources().getColor(android.R.color.transparent)};
        GradientDrawable bpDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colorArr);
        bottomPanel.setBackground(bpDrawable);


        videoView = findViewById(R.id.play_view);
//        videoView.setOnTouchListener(this);
        seekBar = findViewById(R.id.progress_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                android.util.Log.d(TAG, "onProgressChanged: " + progress + " " + fromUser);
                this.progress = progress;
                if (!preparing) {
                    lastPos = progress;
                }
                if (fromUser) {
                    delayBottomPanelHide();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                android.util.Log.d(TAG, "onStartTrackingTouch: ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                android.util.Log.d(TAG, "onStopTrackingTouch: ");
                delayBottomPanelHide();
                if (mediaPlayer != null) {
                    android.util.Log.d(TAG, "seekto: " + progress);
                    videoView.seekTo(progress);
                }
            }
        });

        progressTv = findViewById(R.id.progress_tv);
        durationTv = findViewById(R.id.duration_tv);
        playPauseImg = findViewById(R.id.action);
        findViewById(R.id.back).setOnClickListener(this);

        preparing = true;
        Uri uri = Uri.parse(video.url);
//        Uri uri = Uri.parse("https://oss.5rs.me/oss/transcode/video/mp4/b3fc8e3fefb83281a2c1ff89686b75b3_20190109091356553.mp4");
        android.util.Log.d(TAG, "uri: " + uri.toString() + " [ " + uri.getScheme() + "]");
        try {
            videoView.setVideoURI(uri);
            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                android.util.Log.d(TAG, "onPrepared: " + lastPos);
                mediaPlayer = mp;
                if (lastPos >= 0) {
                    videoView.seekTo(lastPos);
                    lastPos = -1;
                }

                seekBar.setProgress(0);
                progressTv.setText(DateUtil.parseDuration(0));

                seekBar.setMax(mp.getDuration());
                durationTv.setText(DateUtil.parseDuration(mp.getDuration()));

                preparing = false;
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                android.util.Log.d(TAG, "onInfo: " + mp + " " + what + " " + extra);
                if (what == MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING ||
                        what == MediaPlayer.MEDIA_INFO_VIDEO_NOT_PLAYING) {
                    retry();
                    return true;
                }
                return false;
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                android.util.Log.d(TAG, "onError: " + mp + " " + what + " " + extra);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        retry();
                    }
                });
                return true;
            }
        });

        playPauseImg.setOnClickListener(this);

//        if (episode.questionTsList != null) {
////            questionShownList = new ArrayList<>(episode.questionTsList.size());
//            currQuestionIndex = 0;
//            currQuestionTs = episode.questionTsList.get(currQuestionIndex);
//        }

        handler.sendEmptyMessage(MSG_PROGRESS_QUERY);

        handler.sendEmptyMessage(MSG_SHOW_BOTTOM_PANEL);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    private int currQuestionIndex = -1;
    private int currQuestionTs = -1;
//    private List<Boolean> questionShownList;

    private int pausePos;

    private static final long DURATION_HIDE_BOTTOM_PANEL = 3000L;

    private static final int MSG_PROGRESS_QUERY = 1;
    private static final int MSG_HIDE_BOTTOM_PANEL = 2;
    private static final int MSG_SHOW_BOTTOM_PANEL = 3;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_QUERY:
                    int currPos = videoView.getCurrentPosition();
                    seekBar.setProgress(currPos);
                    progressTv.setText(DateUtil.parseDuration(currPos));
//                    if (currQuestionTs > 0 && videoView.getCurrentPosition() > currQuestionTs) {
//                        new QuestionDialog(com.ruitech.tvbookstudy.PlayActivity.this, episode.questionList.get(currQuestionIndex), com.ruitech.tvbookstudy.PlayActivity.this).show();
//                        videoView.pause();
//                        pausePos = currPos;
//
//                        // calculate next.
//                        if (currQuestionIndex >= episode.questionTsList.size() - 1) {
//                            // nothing more, so reset.
//                            currQuestionIndex = -1;
//                            currQuestionTs = -1;
//                        } else {
//                            currQuestionIndex++;
//                            currQuestionTs = episode.questionTsList.get(currQuestionIndex);
//                        }
//                    } else {
                        handler.sendEmptyMessageDelayed(MSG_PROGRESS_QUERY, 500L);
//                    }
                    break;
                case MSG_HIDE_BOTTOM_PANEL:
                    bottomPanel.setVisibility(View.INVISIBLE);
                    playPauseImg.setVisibility(View.INVISIBLE);
                    seekBar.setVisibility(View.INVISIBLE);
                    progressTv.setVisibility(View.INVISIBLE);
                    durationTv.setVisibility(View.INVISIBLE);
                    bottomPanelShown = false;
                    break;
                case MSG_SHOW_BOTTOM_PANEL:
                    bottomPanel.setVisibility(View.VISIBLE);
                    playPauseImg.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    progressTv.setVisibility(View.VISIBLE);
                    durationTv.setVisibility(View.VISIBLE);
                    bottomPanelShown = true;
                    handler.sendEmptyMessageDelayed(MSG_HIDE_BOTTOM_PANEL, DURATION_HIDE_BOTTOM_PANEL);
                    break;
            }
        }
    };

    private boolean bottomPanelShown;

//    @Override
//    public void onQuestionSelected(Question question, boolean right) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                android.util.Log.d(TAG, "onQuestionSelected: " + pausePos);
//
//                Dialog d = new ResultDialog(com.ruitech.tvbookstudy.PlayActivity.this, question, right);
//                d.setOnDismissListener(com.ruitech.tvbookstudy.PlayActivity.this);
//                d.show();
//
//            }
//        });
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        videoView.stopPlayback();
        mediaPlayer = null;
    }

    private boolean preparing;
    private void retry() {
        int lastPos = videoView.getCurrentPosition();
        if (lastPos > 0) {
            this.lastPos = lastPos;
        }

        android.util.Log.d(TAG, "retry: " + lastPos + " " + this.lastPos);
        videoView.stopPlayback();
        mediaPlayer = null;

        preparing = true;
        Uri uri = Uri.parse(video.url);
        android.util.Log.d(TAG, "uri: " + uri.toString() + " [ " + uri.getScheme() + "]");
        try {
            videoView.setVideoURI(uri);
            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        android.util.Log.d(TAG, "onKeyUp: " + keyCode + " " + event.getAction());
//        if (event.getAction() == KeyEvent.ACTION_UP && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
//
//            if (mediaPlayer != null) {
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();
//                } else {
//                    mediaPlayer.start();
//                }
//            }
//            return true;
//        }
//
//        return super.onKeyUp(keyCode, event);
//    }

//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        if (mediaPlayer != null){
//            mediaPlayer.start();
//            handler.sendEmptyMessageDelayed(MSG_PROGRESS_QUERY, 500L);
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action:
                if (mediaPlayer != null) {
                    if (videoView.isPlaying()) {
                        mediaPlayer.pause();
                        playPauseImg.setImageResource(R.mipmap.play2);
                    } else {
                        mediaPlayer.start();
                        playPauseImg.setImageResource(R.mipmap.pause2);
                    }
                }
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        this.onTouchEvent()
//        switch (v.getId()) {
//            case R.id.play_view:
//
//        }
//        return false;
//    }

    private void delayBottomPanelHide() {
        handler.removeMessages(MSG_HIDE_BOTTOM_PANEL);
        handler.sendEmptyMessageDelayed(MSG_HIDE_BOTTOM_PANEL, DURATION_HIDE_BOTTOM_PANEL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomPanelShown) {
                delayBottomPanelHide();
            } else {
                handler.sendEmptyMessage(MSG_SHOW_BOTTOM_PANEL);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        android.util.Log.d(TAG, "onResume: " + mediaPlayer + " " + lastPos);
        if (mediaPlayer != null && lastPos >= 0) {
            videoView.start();
            playPauseImg.setImageResource(R.mipmap.pause2);
        }
    }

    private int lastPos = -1;
    @Override
    protected void onPause() {
        if (mediaPlayer != null) {
            lastPos = videoView.getCurrentPosition();
            videoView.pause();
        }
        super.onPause();
        android.util.Log.d(TAG, "onPause: " + mediaPlayer + " " + lastPos);
    }
}
