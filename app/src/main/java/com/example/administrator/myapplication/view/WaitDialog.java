package com.example.administrator.myapplication.view;


import com.buyfood.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * @author http://blog.csdn.net/finddreams
 * @Description:自定义对话框
 */
public class WaitDialog extends ProgressDialog {

    private AnimationDrawable mAnimation;
    private Context mContext;
    private ImageView mImageView;

    public WaitDialog(Context context) {
        super(context);
        this.mContext = context;
        setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void show() {
        if (mImageView != null) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.aini_loading);
            mImageView.startAnimation(animation);
        }
        super.show();
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            mImageView.clearAnimation();
            super.dismiss();
        }
    }

    private void initView() {
        setContentView(R.layout.progress_dialog);
        mImageView = (ImageView) findViewById(R.id.loadingIv);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.aini_loading);
        mImageView.startAnimation(animation);
    }


}
