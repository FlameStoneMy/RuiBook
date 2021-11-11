package com.ruitech.bookstudy;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseDialog extends AppCompatDialog {

    private View shaderView;
    public BaseDialog(Context context, int theme) {
        super(context, theme);

        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        initWindow(window);

        ConstraintLayout f = new ConstraintLayout(context);
        f.setId(R.id.parent);
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        f.setLayoutParams(params);
        v = getLayoutInflater().inflate(getLayoutId(), null);
        v.setId(R.id.body_container);
        ConstraintLayout.LayoutParams vParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vParams.leftToLeft = R.id.parent;
        vParams.rightToRight = R.id.parent;
        f.addView(v, vParams);

        shaderView = new View(context);
        shaderView.setBackground(new ColorDrawable(context.getResources().getColor(R.color._26ffb334)));
        ConstraintLayout.LayoutParams sParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        sParams.leftToLeft = R.id.body_container;
        sParams.rightToRight = R.id.body_container;
        sParams.topToTop = R.id.body_container;
        sParams.bottomToBottom = R.id.body_container;
        f.addView(shaderView, sParams);

        setContentView(f, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        shaderView.setVisibility(App.eyeProtectEnabled() ? View.VISIBLE : View.INVISIBLE);
    }

    protected void initWindow(Window window) {
    }

    protected View v;
    protected abstract int getLayoutId();
}
