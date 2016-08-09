package com.example.administrator.myapplication.utils;

import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.example.administrator.myapplication.globle.BfApplication;

/**
 * 封装一些零碎的工具方法
 * 
 * @author Administrator
 * 
 */
public class CommonUtil {

	public static int getAppVerCode() {
		int verCode = -1;
		try {
			verCode = BfApplication
					.getInstance()
					.getPackageManager()
					.getPackageInfo(
							BfApplication.getInstance().getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e("", e.getMessage());
		}
		return verCode;
	}
}
