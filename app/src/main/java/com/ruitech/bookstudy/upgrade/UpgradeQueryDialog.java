package com.ruitech.bookstudy.upgrade;

import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ruitech.bookstudy.BaseDialog;
import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.utils.UIHelper;

import androidx.appcompat.app.AppCompatDialog;

public class UpgradeQueryDialog extends BaseDialog implements View.OnClickListener {
    private UpgradeBean upgradeBean;
    private Callback callback;
    public UpgradeQueryDialog(Context context, UpgradeBean upgradeBean, Callback callback) {
        super(context, R.style.ThemeDialog);

        this.upgradeBean = upgradeBean;
        this.callback = callback;

        ((TextView)v.findViewById(R.id.title)).setText(
                context.getResources().getString(R.string.version_upgrade_desc, upgradeBean.versionName));

        v.findViewById(R.id.action).setOnClickListener(this);
    }

    @Override
    protected void initWindow(Window window) {
        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setCornerRadius(UIHelper.dp2px(18));
        paintDrawable.getPaint().setColor(getContext().getResources().getColor(R.color._ffffff));
        window.setBackgroundDrawable(paintDrawable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action:
                callback.onVersionUpgrade(upgradeBean);
                dismiss();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_upgrade_query;
    }

    public interface Callback {
        void onVersionUpgrade(UpgradeBean upgradeBean);
    }
}
