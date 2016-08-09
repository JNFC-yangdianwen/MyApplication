package com.example.administrator.myapplication.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.buyfood.R;

public class LoadingDialog extends Dialog{

	public LoadingDialog(Context context,int theme,String message) {
		super(context,theme);
		// TODO Auto-generated constructor stub
		View view = View.inflate(context, R.layout.customprogressdialog, null);
		setContentView(view);
		TextView msg = (TextView) view.findViewById(R.id.message);
		msg.setText(message);
		this.setCancelable(true);
		this.setCanceledOnTouchOutside(false);
	}
}
