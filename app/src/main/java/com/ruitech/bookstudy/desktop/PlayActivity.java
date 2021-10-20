package com.ruitech.bookstudy.desktop;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.desktop.bean.Video;
import com.ruitech.bookstudy.utils.DateUtil;
import com.ruitech.bookstudy.utils.StatusBarUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlayActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private static final String TAG = "PlayActivity";

    private static final String EXTRA_VIDEO = "video";
    public static void start(Context context, Video video) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
        context.startActivity(intent);
    }

    private ProgressBar progressBar;
    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private Video video;
    private TextView progressTv, durationTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.fullScreen(this);

        video = (Video) getIntent().getSerializableExtra(EXTRA_VIDEO);

        setContentView(R.layout.activity_play);
        videoView = findViewById(R.id.play_view);
        progressBar = findViewById(R.id.progress_bar);
        progressTv = findViewById(R.id.progress_tv);
        durationTv = findViewById(R.id.duration_tv);

        Uri uri = Uri.parse(video.url);
        videoView.setVideoURI(uri);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                progressBar.setMax(mp.getDuration());
                progressBar.setProgress(0);

                progressTv.setText(DateUtil.parseDuration(0));
                durationTv.setText(DateUtil.parseDuration(mp.getDuration()));
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                android.util.Log.d(TAG, "onInfo: " + mp + " " + what + " " + extra);
                return false;
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                android.util.Log.d(TAG, "onError: " + mp + " " + what + " " + extra);
                return true;
            }
        });

//        if (episode.questionTsList != null) {
////            questionShownList = new ArrayList<>(episode.questionTsList.size());
//            currQuestionIndex = 0;
//            currQuestionTs = episode.questionTsList.get(currQuestionIndex);
//        }

        handler.sendEmptyMessage(MSG_PROGRESS_QUERY);
    }

    private int currQuestionIndex = -1;
    private int currQuestionTs = -1;
//    private List<Boolean> questionShownList;

    private int pausePos;
    private static final int MSG_PROGRESS_QUERY = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_QUERY:
                    if (mediaPlayer != null) {
                        android.util.Log.d(TAG, "" + videoView.getCurrentPosition() + " " + videoView.getDuration() + " " + mediaPlayer.getCurrentPosition() + " " + mediaPlayer.getDuration());
                    }

                    int currPos = videoView.getCurrentPosition();
                    progressBar.setProgress(currPos);
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
            }
        }
    };

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
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        android.util.Log.d(TAG, "onKeyUp: " + keyCode + " " + event.getAction());
        if (event.getAction() == KeyEvent.ACTION_UP && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {

            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mediaPlayer != null){
            mediaPlayer.start();
            handler.sendEmptyMessageDelayed(MSG_PROGRESS_QUERY, 500L);
        }
    }
}
