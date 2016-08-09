package com.example.administrator.myapplication.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

import com.buyfood.R;
import com.example.administrator.myapplication.view.LoadingDialog;

/**
 * 
 * @author yuemx 创建时间：2015年12月15日 下午1:26:39
 * 
 *         类 描述 UI辅助类
 */
public class UIUtils {

	public static final String FilEFIX = "file://";

	private static Dialog mProDialog;

	public static Dialog getDialog(Context c, String message) {
		mProDialog = new LoadingDialog(c, R.style.add_dialog, message);
		mProDialog.show();
		mProDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mProDialog = null;
			}
		});
		return mProDialog;
	}

	public static String getFilePath(String Path) {
		return FilEFIX + Path;
	}

}
