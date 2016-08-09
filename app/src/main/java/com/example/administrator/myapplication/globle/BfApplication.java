package com.example.administrator.myapplication.globle;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.example.administrator.myapplication.model.FoodCarModel;
import com.example.administrator.myapplication.utils.InitIMLUtils;

import java.util.HashMap;
import java.util.Map;

public class BfApplication extends Application {

	public static Context appContext;
	private static BfApplication instance;
	// public static String city ="全国";
	public static String province = "";
	public static double latitude; // 纬度
	public static double longitude; // 经度
	public static String addr = ""; // 位置

	public static Map<String, FoodCarModel> carMap = new HashMap<>();

	public static BfApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		// 设置全局异常扑捉
		// CrashHandler crashHandler=CrashHandler.getInstance();
		// crashHandler.init(this);
		// ImageLoader类库初始化配置 图片加载框架
		InitIMLUtils.initImageLoad(getApplicationContext());
		SDKInitializer.initialize(this);

	}

}
