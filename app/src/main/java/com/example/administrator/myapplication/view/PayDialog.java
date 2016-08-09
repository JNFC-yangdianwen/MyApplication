package com.example.administrator.myapplication.view;

import com.buyfood.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class PayDialog extends Dialog{

	public  View tv_cancel;
	private int windowHeight;
	private int windowWidth;



	public PayDialog(Context context) {
		super(context, R.style.customDialog);
		setView(context);
	}

	
	
	/**
	 *1.初始化界面
	 * @param layoutResID
	 */
	public void setView(Context context) {
		setContentView(R.layout.dialog_layout_pay);
		tv_cancel = findViewById(R.id.tv_cancel);
		 WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		  Display display = manager.getDefaultDisplay();
		  windowHeight	= display.getHeight();
		  windowWidth	= display.getWidth();
		Window dialogWindow = getWindow();  
		dialogWindow.setGravity( Gravity.BOTTOM);  //设置显示位置
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setAttributes(lp);
		
		lp.width = windowWidth; 
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);

	}


}
