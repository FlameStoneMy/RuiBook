package com.ruitech.bookstudy;

import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.view.View;
import android.view.Window;

import com.ruitech.bookstudy.utils.UIHelper;

import androidx.appcompat.app.AppCompatDialog;

public class PermDialog extends BaseDialog implements View.OnClickListener {
    public PermDialog(Context context) {
        super(context, R.style.ThemeDialog);

        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setCornerRadius(UIHelper.dp2px(18));
        paintDrawable.getPaint().setColor(context.getResources().getColor(R.color._ffffff));
        getWindow().setBackgroundDrawable(paintDrawable);

        v.findViewById(R.id.action).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action:
                dismiss();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_perm;
    }
}
