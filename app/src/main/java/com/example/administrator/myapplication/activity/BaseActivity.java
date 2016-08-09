package com.example.administrator.myapplication.activity;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.utils.MyActivityManager;
import com.example.administrator.myapplication.view.WaitDialog;

public class BaseActivity extends Activity{
	protected ViewGroup mainBody; // 主体显示
	public WaitDialog waitDialog;  //等待進度框

	protected boolean isTemplate = false; // 是否使用模板
	private View titleBar;// 标题栏
	protected TextView titleView; // 标题
	protected ImageButton templateButtonLeft; // 模板中左则Button,不使用模板时，不会显示;
	protected ImageButton templateButtonRight; // 模板中右则Button,不使用模板时，不会显示;
	protected TextView templateTextViewRight; // 模板右侧TextView
	private View reLoadView;// 重新加载的 布局
	private MyActivityManager myActivityManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template);
		mainBody = (ViewGroup) findViewById(R.id.view_mainBody);
		reLoadView = findViewById(R.id.reLoadView);
		reLoadView.setVisibility(View.GONE);
		titleBar = findViewById(R.id.titleBar);
		templateButtonLeft = (ImageButton) findViewById(R.id.title_but_left);
		templateButtonRight = (ImageButton) findViewById(R.id.title_but_right);
		templateTextViewRight = (TextView) findViewById(R.id.title_tv_right);
		
		 waitDialog = new WaitDialog(this);
		templateButtonLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleView = (TextView) findViewById(R.id.title_text);
		if (!isTemplate) {// 不使用模板就把标题栏去掉
			titleBar.setVisibility(View.GONE);
		}
//		将Activity压入栈中管理起来
		 myActivityManager = MyActivityManager.getInstance();
		myActivityManager.pushOneActivity(this);
	}
	
	
	@Override
	public void setContentView(int layoutResID) {
		if (layoutResID == R.layout.template) {
			super.setContentView(layoutResID);
		} else {
			mainBody.removeAllViews();
			mainBody.addView(this.getLayoutInflater().inflate(layoutResID, null));
		}
	}
	
	@Override
	public void setContentView(View view) {
		mainBody.removeAllViews();
		mainBody.addView(view);
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		mainBody.removeAllViews();
		mainBody.addView(view, params);
	}
//	显示Toast消息
	public void showShortToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	
	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		销毁时先异常Activity
		myActivityManager.pushOneActivity(this);
		
	    if (waitDialog != null) {
            if (waitDialog.isShowing()) {
                waitDialog.dismiss();
            }
        }
        System.gc();
	}
}
