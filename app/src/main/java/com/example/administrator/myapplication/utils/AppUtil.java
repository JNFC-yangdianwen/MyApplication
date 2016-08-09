package com.example.administrator.myapplication.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.GetVerResponse;
import com.example.administrator.myapplication.view.LoadingDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * APP工具类
 * 
 * @author Gc
 * 
 */
public class AppUtil {

	public static void doNewVersionUpdate(final GetVerResponse Config,
			final Context context) {
		Dialog dialog = new AlertDialog.Builder(context).setTitle("软件更新")
				.setMessage("当前不是最新版本，需要更新到最新版本")
				// 设置内容
				.setPositiveButton("马上升级",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								LoadingDialog mLoadingDialog = (LoadingDialog) UIUtils
										.getDialog(context, "正在下载,请稍候...");
								if (Config.data.url != null) {
									AppUtil.downFile(Config, mLoadingDialog,
											context); // 下载
								}
							}
						}).create();// 创建
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		dialog.show();
	}

	public static void downFile(final GetVerResponse body,
			final LoadingDialog mLoadingDialog, final Context context) {
		mLoadingDialog.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(Contants.GLOBAL_URL + body.data.url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(
								Environment.getExternalStorageDirectory(),
								Contants.APP_NAME);
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							if (length > 0) {
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					update(mLoadingDialog, context, body);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	// 安装应用
	static void update(LoadingDialog mLoadingDialog, Context context,
			GetVerResponse body) {
		mLoadingDialog.dismiss();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), Contants.APP_NAME)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
		// 点击"取消"按钮之后退出程序
		// 是否强制更新
		System.exit(0);
	}
}
