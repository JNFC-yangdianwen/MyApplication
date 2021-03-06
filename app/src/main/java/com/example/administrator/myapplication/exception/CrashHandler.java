package com.example.administrator.myapplication.exception;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 捕获全局异常
 * 
 * @version V1.0 <描述当前版本功能>
 * @FileName: net.trasin.health.exception.CrashHandler.java
 * @author: liumj
 * @date: 2015-07-03 14:07
 * @Developer Kits:AndroidStudio
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

	private static final String Error_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/buyfood/error/";
	/**
	 * CrashHandler实例
	 */

	private static CrashHandler instance;
	private Context mContext;

	/**
	 * 获取CrashHandler实例 ,单例模式
	 */

	public static CrashHandler getInstance() {
		if (instance == null) {
			instance = new CrashHandler();
		}
		return instance;
	}

	/**
	 * 异常发生时，系统回调的函数，我们在这里处理一些操作
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// 将一些信息保存到SDcard中
		savaInfoToSD(mContext, ex);

		// 提示用户程序即将退出
		showToast(mContext, "很抱歉，程序遭遇异常，即将退出！");
	}

	/**
	 * 为我们的应用程序设置自定义Crash处理
	 */
	public void init(Context context) {
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 显示提示信息，需要在线程中显示Toast
	 * 
	 * @param context
	 * @param msg
	 */
	private void showToast(final Context context, final String msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}).start();
	}

	/**
	 * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
	 * 
	 * @param context
	 * @return
	 */
	private HashMap<String, String> obtainSimpleInfo(Context context) {
		HashMap<String, String> map = new HashMap<String, String>();
		PackageManager mPackageManager = context.getPackageManager();
		PackageInfo mPackageInfo = null;
		try {
			mPackageInfo = mPackageManager.getPackageInfo(
					context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		assert mPackageInfo != null;
		map.put("versionName", mPackageInfo.versionName);
		map.put("versionCode", "" + mPackageInfo.versionCode);

		map.put("MODEL", "" + Build.MODEL);
		map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
		map.put("PRODUCT", "" + Build.PRODUCT);

		return map;
	}

	/**
	 * 获取系统未捕捉的错误信息
	 * 
	 * @param throwable
	 * @return
	 */
	private String obtainExceptionInfo(Throwable throwable) {
		StringWriter mStringWriter = new StringWriter();
		PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
		throwable.printStackTrace(mPrintWriter);
		mPrintWriter.close();
		return mStringWriter.toString();
	}

	/**
	 * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
	 * 
	 * @param context
	 * @param ex
	 * @return
	 */
	private String savaInfoToSD(Context context, Throwable ex) {
		String fileName = null;
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, String> entry : obtainSimpleInfo(context)
				.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append(" = ").append(value).append("\n");
		}

		sb.append(obtainExceptionInfo(ex));
		Log.e("errlog", sb.toString());

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File dir = new File(Error_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			try {
				fileName = dir.getPath() + File.separator
						+ paserTime(System.currentTimeMillis()) + ".log";
				FileOutputStream fos = new FileOutputStream(fileName);
				fos.write(sb.toString().getBytes());
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return fileName;

	}

	/**
	 * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
	 * 
	 * @param milliseconds
	 * @return
	 */
	private String paserTime(long milliseconds) {
		System.setProperty("user.timezone", "Asia/Shanghai");
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss",
				Locale.CHINA);

		return format.format(new Date(milliseconds));
	}

}
