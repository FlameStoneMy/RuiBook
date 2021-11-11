package com.ruitech.bookstudy.settings;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.ruitech.bookstudy.BaseDialog;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.RuiPreferenceUtil;
import com.ruitech.bookstudy.utils.UIHelper;

import org.greenrobot.eventbus.EventBus;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsDialog extends BaseDialog implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SettingsDialog";

    public SettingsDialog(Context context) {
        super(context, R.style.ThemeDialog);

        AppCompatSeekBar speedSeekBar = v.findViewById(R.id.speed_seekbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            findViewById(R.id.adjust_speed_tv).setVisibility(View.GONE);
            findViewById(R.id.scale1).setVisibility(View.GONE);
            findViewById(R.id.scale2).setVisibility(View.GONE);
            findViewById(R.id.scale3).setVisibility(View.GONE);
            findViewById(R.id.scale4).setVisibility(View.GONE);
            ((ViewGroup.MarginLayoutParams) findViewById(R.id.show_translation_tv).getLayoutParams()).topMargin = 0;
            speedSeekBar.setVisibility(View.GONE);
        } else {
            speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                private int progress;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d(TAG, "onProgressChanged: " + progress + " " + fromUser);
                    this.progress = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d(TAG, "onStopTrackingTouch");
                    int tarProgress;
                    if (progress < 17) {
                        tarProgress = 0;
                    } else if (progress < 49) {
                        tarProgress = 33;
                    } else if (progress < 83) {
                        tarProgress = 67;
                    } else {
                        tarProgress = 100;
                    }
                    if (tarProgress != progress) {
                        seekBar.setProgress(tarProgress);
                    }
                    RuiPreferenceUtil.setClickReadSpeed(progress2Speed(progress));
                    EventBus.getDefault().post(new ChangeSpeedEvent());
                }
            });
            speedSeekBar.setProgress(speed2Progress(RuiPreferenceUtil.getClickReadSpeed()));
        }

        SwitchCompat showTranslationSwitch = (SwitchCompat) v.findViewById(R.id.show_translation_switch);
        showTranslationSwitch.setChecked(RuiPreferenceUtil.showClickReadTranslation());
        showTranslationSwitch.setOnCheckedChangeListener(this);

        SwitchCompat showBordersSwitch = (SwitchCompat) v.findViewById(R.id.show_click_read_borders_switch);
        showBordersSwitch.setChecked(RuiPreferenceUtil.showClickReadBorders());
        showBordersSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initWindow(Window window) {
        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setCornerRadius(UIHelper.dp2px(18));
        paintDrawable.getPaint().setColor(getContext().getResources().getColor(R.color._ffffff));
        window.setBackgroundDrawable(paintDrawable);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_settings;
    }

    private float progress2Speed(int progress) {
        float ret;
        if (progress < 33) {
            ret = 0.5F;
        } else if (progress < 67) {
            ret = 1F;
        } else if (progress < 100) {
            ret = 1.5F;
        } else {
            ret = 2F;
        }

        return ret;
    }

    private int speed2Progress(float speed) {
        int ret;
        if (speed < 1F) {
            ret = 0;
        } else if (speed < 1.5F) {
            ret = 33;
        } else if (speed < 2F) {
            ret = 67;
        } else {
            ret = 100;
        }
        return ret;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.show_translation_switch:
                RuiPreferenceUtil.setShowClickReadTranslation(isChecked);
                break;
            case R.id.show_click_read_borders_switch:
                RuiPreferenceUtil.setShowClickReadBorders(isChecked);
                EventBus.getDefault().post(new ShowBordersEvent());
                break;
        }
    }
}
