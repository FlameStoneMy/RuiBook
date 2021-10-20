package com.ruitech.bookstudy.upgrade;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruitech.bookstudy.R;
import com.ruitech.bookstudy.utils.Executors;
import com.ruitech.bookstudy.utils.UIHelper;

import java.io.File;

import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.FileProvider;

public class UpgradeDialog extends AppCompatDialog implements View.OnClickListener, UpgradeTask.Callback {
    private UpgradeBean upgradeBean;

    private ProgressBar progressBar;
    public UpgradeDialog(Context context, UpgradeBean upgradeBean) {
        super(context, R.style.ThemeDialog);

        this.upgradeBean = upgradeBean;

        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);

        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setCornerRadius(UIHelper.dp2px(18));
        paintDrawable.getPaint().setColor(context.getResources().getColor(R.color._ffffff));
        window.setBackgroundDrawable(paintDrawable);
        View v = getLayoutInflater().inflate(R.layout.dialog_upgrade, null);

        progressBar = v.findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        v.findViewById(R.id.action).setOnClickListener(this);
        setContentView(v);

        this.setCanceledOnTouchOutside(false);
        upgradeTask = (UpgradeTask) new UpgradeTask(upgradeBean, this).executeOnExecutor(Executors.io());
    }

    private UpgradeTask upgradeTask;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action:
                upgradeTask.cancel(true);
                dismiss();
                break;
        }
    }

    @Override
    public void onUpgradeProgress(int progress) {

    }

    @Override
    public void onNewVersionFile(File apkFile) {
        if (apkFile != null) {
            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//            installIntent.setData(Uri.fromFile(apkFile));
            installIntent.setData(
                    FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", apkFile));
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getContext().startActivity(installIntent);
        }

        dismiss();
    }
}
