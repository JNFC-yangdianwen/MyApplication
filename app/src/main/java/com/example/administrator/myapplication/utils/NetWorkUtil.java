package com.example.administrator.myapplication.utils;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 * 
 *    
 * ��Ŀ��ƣ�Framework   
 * ����ƣ�NetWorkUtil   
 * ���������������繤����   
 * �����ˣ�������  
 * �޸ı�ע��    
 *
 */

//�ж��û��Ƿ���������
public class NetWorkUtil {
	public static boolean isNetAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isAvailable()) {
			return false;
		}
		return true;
	}

	// �жϵ�ǰ�Ƿ�ʹ�õ��� WIFI����  
	public static boolean isWifiActive(Context icontext){
		Context context = icontext.getApplicationContext();    
		ConnectivityManager connectivity = (ConnectivityManager) context    
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info;
		if (connectivity != null) {    
			info = connectivity.getAllNetworkInfo();    
			if (info != null) {    
				for (int i = 0; i < info.length; i++) {    
					if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {    
						return true;    
					}    
				}    
			}    
		}    
		return false;   
	}


	//Get
	public static String PHPSESSID;
	public static String GetDate(String strurl)
	{
		Log.e("seesion请求",strurl+"");
		String result="";
		HttpGet mHttpGet=new HttpGet(strurl);
		mHttpGet.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
		HttpParams mHttpParams=new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(mHttpParams,15000);
		HttpConnectionParams.setSoTimeout(mHttpParams, 15000);
		DefaultHttpClient mHttpClient=new DefaultHttpClient(mHttpParams);
		try {
			HttpResponse mHttpResponse=mHttpClient.execute(mHttpGet);
			if(mHttpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
			{
				//��ȡsession	
				CookieStore mCookieStore = mHttpClient.getCookieStore();
				List<Cookie> cookies = mCookieStore.getCookies();
				for (int i = 0; i < cookies.size(); i++) {
					//�����Ƕ�ȡCookie['PHPSESSID']��ֵ���ھ�̬�����У���֤ÿ�ζ���ͬһ��ֵ
					if ("PHPSESSID".equals(cookies.get(i).getName())) {
						PHPSESSID = cookies.get(i).getValue();
						break;
					}
				}
				if(PHPSESSID != null){
					System.out.println(PHPSESSID);
				}
				HttpEntity mHttpEntity=mHttpResponse.getEntity();
				result=EntityUtils.toString(mHttpEntity,"utf-8");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * ���������ý���
	 */
	public static void openSetting(Activity activity)
	{
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}
}