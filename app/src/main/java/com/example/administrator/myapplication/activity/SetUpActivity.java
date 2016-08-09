package com.example.administrator.myapplication.activity;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.InviteCodeBean;
import com.example.administrator.myapplication.model.VersionInfoBean;
import com.example.administrator.myapplication.model.VersionInfoBean.VersionInfo;
import com.example.administrator.myapplication.utils.DataCleanManager;
import com.example.administrator.myapplication.utils.UserUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
/**
 * 设置页面
 * @author fengzi
 *
 */
public class SetUpActivity extends BaseActivity implements OnClickListener{

	private View tv_sugges;
	private TextView tv_clean;
	private String totalCacheSize;
	private String APP_PATH;
	protected String version_code;
	protected String url;
	private View tv_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("设置");
		setContentView(R.layout.activity_setup);
		 tv_sugges = findViewById(R.id.tv_sugges);
		 tv_clean = (TextView)findViewById(R.id.tv_clean);
		 tv_update = findViewById(R.id.tv_update);
		 initData();
		 tv_sugges.setOnClickListener(this);
		 tv_clean.setOnClickListener(this);
		 tv_update.setOnClickListener(this);
		 
		 try {
			 totalCacheSize = DataCleanManager.getTotalCacheSize(getApplicationContext());
//			 Toast.makeText(this, ""+totalCacheSize, Toast.LENGTH_SHORT).show();
			 tv_clean.setText("清除缓存（"+totalCacheSize+"）");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_sugges:
			if("no".equals(UserUtil.getUsrId(this))){
				Intent intent = new Intent(this,LoginActivity.class);
	    		startActivity(intent); 
	    		return;
			} 
			Intent intent = new Intent(this,SuggesActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_update:
			update();
			break;
		case R.id.tv_clean:  //清除本应用内部缓存
			DataCleanManager.cleanInternalCache(getApplicationContext());
			Toast.makeText(this, "已清除", Toast.LENGTH_SHORT).show();
			 try {
				 totalCacheSize = DataCleanManager.getTotalCacheSize(getApplicationContext());
				 Toast.makeText(this, ""+totalCacheSize, Toast.LENGTH_SHORT).show();
				 tv_clean.setText("清除缓存（"+totalCacheSize+"）");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}



	
	  /**
	   * 网络请求
	   */
	  public void initData(){
		  
		  waitDialog.show();
		  String usrId = UserUtil.getUsrId(this);
		  HttpUtils http = new HttpUtils();
//	         添加请求参数 
		  http.send(HttpMethod.POST, Contants.versionUndate, new RequestCallBack<String>() { 
			  @Override 
			  public void onSuccess(ResponseInfo<String> responseInfo) { 
				  int statusCode = responseInfo.statusCode;
				  if(statusCode == 200){
					  String result = responseInfo.result;
					  Log.e("userinfoactivity", result+"");
					  Gson gson = new Gson();
					  VersionInfoBean versionInfoBean = gson.fromJson(result, VersionInfoBean.class);
					  waitDialog.dismiss();
					  if("1".equals(versionInfoBean.code)){
						  version_code = versionInfoBean.data.version_code;
						  url = versionInfoBean.data.url;
//						  完成操作
					  }
					  
				  }
			  } 
			  
			  
			  @Override 
			  public void onFailure(HttpException error, String msg) { 
				  waitDialog.dismiss();
				  showShortToastMessage("网络加载异常，请稍后再试");
			  } 
			  
		  }); 
	  }
	
	
	
	
//	检查更新
	public void update(){
		if(version_code!=null){
			if(version_code.equals(version_code)){
				Toast.makeText(SetUpActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
			}else{
				if(url!=null&&!"".equals(url)){
					 Builder builder = new Builder(SetUpActivity.this);
					 builder.setTitle("温馨提示");
					 builder.setMessage("版本已更新，是否下载"+version_code+"版");
	
					 
					  builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
	
						   @Override
						   public void onClick(DialogInterface dialog, int which) {
							   download(Contants.GLOBAL_URL+url);  //下载最新版本
						    dialog.dismiss();
						   }
						  });
	
						  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	
						   @Override
						   public void onClick(DialogInterface dialog, int which) {
						    dialog.dismiss();
						   }
						  });
						  
						  builder.create().show();
					 
					
				} 
			}
		}
}
	
	
	
	
	
	
	
	
	
	

//	下载apk   
	public void download(String apkUrl){
	
		
		APP_PATH = Environment.getExternalStorageDirectory().getPath() + "/maicai/app/";
        File dir = new File(APP_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

		HttpUtils http = new HttpUtils();
//		test
		HttpHandler handler = http.download(apkUrl,APP_PATH+"maicai"+version_code+".apk",
				
//		HttpHandler handler = http.download(Contants.PHOTO_URL+url,APP_PATH+"maicaiww.apk",
		    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
		    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
		    new RequestCallBack<File>() {


				private ProgressBar progressBar1;
				private Builder builder;
				private AlertDialog alertDialog;
				private TextView tv_jd;

				@Override
		        public void onStart() {
					 View view = getLayoutInflater().inflate(R.layout.download, null);
					 progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
					 tv_jd = (TextView) view.findViewById(R.id.tv_jd);
					
					 builder = new Builder(SetUpActivity.this);
					 builder.setTitle("下载中...");
			       	 builder.setCancelable(false);
			    	 builder.setView(view);
			    	 alertDialog = builder.create();
//		        	Toast.makeText(AbuoutActivity.this, "开始下载", 0).show();
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	 int bl =(int) ((double)current/(double)total*100);
		        	 System.out.println(bl);
		        	 tv_jd.setText(bl+"%");
		        	 progressBar1.setMax((int)total);
		        	 progressBar1.setProgress((int)current);
		        	 alertDialog.show();
		        	System.out.println(total+"------total-----------------");
		        	System.out.println(current+"------current-----------------");
		        	
		        }

		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		        	Toast.makeText(SetUpActivity.this, "下载成功 保存在:" + responseInfo.result.getPath(), 0).show();
		        	alertDialog.dismiss();
		        	defalutInstall();
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        }
		});
	}
	
//	打开安装APP
	private void defalutInstall() {
        Intent i = new Intent(Intent.ACTION_VIEW); 
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.fromFile(new File(APP_PATH+"maicai"+version_code+".apk")), "application/vnd.android.package-archive"); 
        startActivity(i);
        
        android.os.Process.killProcess(android.os.Process.myPid());
		
	}
}