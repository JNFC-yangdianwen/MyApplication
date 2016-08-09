package com.example.administrator.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.model.MsgBean;
import com.example.administrator.myapplication.model.UserInfoBean;
import com.example.administrator.myapplication.model.UserInfoBean.UserInfo;
import com.example.administrator.myapplication.utils.UserUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import de.greenrobot.event.EventBus;
/**
 * 用户个人信息页面
 * @author fengzi
 *
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener{

	private EditText et_username;
	private EditText et_companyName;
	private EditText et_nickname;
	private Button bt_exit;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("个人信息");
		templateTextViewRight.setVisibility(View.VISIBLE);
		templateTextViewRight.setText("保存");
		setContentView(R.layout.activity_userinfo);
		 et_username = (EditText) findViewById(R.id.et_username);
		 et_nickname = (EditText) findViewById(R.id.et_nickname);
		 et_companyName = (EditText) findViewById(R.id.et_companyName);
		 bt_exit = (Button) findViewById(R.id.bt_exit);
		 initData();
		 templateTextViewRight.setOnClickListener(this);
		 bt_exit.setOnClickListener(this);
	}
	
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_tv_right:
			saveUserInfo();   //保存信息
			break;
		case R.id.bt_exit:
			SharedPreferences userconfig  = getSharedPreferences("userconfig", 0);
			Editor edit = userconfig.edit();
			edit.clear().commit();
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			boolean destroyed = MainActivity.mainActivity.isDestroyed();
			if(!destroyed){
				MainActivity.mainActivity.finish();
			}
			finish();
			break;

		default:
			break;
		}
	}
	
	
	
	
	  /**
	   * 获取信息 网络数据调用
	   */
	  public void initData(){
		  waitDialog.show();
		  String usrId = UserUtil.getUsrId(this);
		  HttpUtils http = new HttpUtils();
		  String url = Contants.userinfo; 
		  RequestParams params = new RequestParams(); 
//	         添加请求参数 
		  params.addBodyParameter("user_id", usrId);
		  http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() { 
			  @Override 
			  public void onSuccess(ResponseInfo<String> responseInfo) { 
				  int statusCode = responseInfo.statusCode;
				  if(statusCode == 200){
					  String result = responseInfo.result;
					  Log.e("userinfoactivity", result+"");
					  Gson gson = new Gson();
					  UserInfoBean userInfoBean = gson.fromJson(result, UserInfoBean.class);
					  waitDialog.dismiss();
					  if("1".equals(userInfoBean.code)){
						  UserInfo data = userInfoBean.data;
						  et_username.setText(data.username);
						  et_companyName.setText(data.companyName);
						  et_nickname.setText(data.nickname);


//						  收货完成操作
					  }else {
						  showShortToastMessage(userInfoBean.msg+"");
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
	  /**
	   * 保存信息
	   */
	  public void saveUserInfo(){
		   String username = et_username.getText().toString();
		   String nickname = et_nickname.getText().toString();
		   String companyName = et_companyName.getText().toString();
		  
		  waitDialog.show();
		  String usrId = UserUtil.getUsrId(this);
		  HttpUtils http = new HttpUtils();
		  String url = Contants.saveUserInfo; 
		  RequestParams params = new RequestParams(); 
//	         添加请求参数 
		  params.addBodyParameter("user_id", usrId);
		  params.addBodyParameter("username", username);
		  params.addBodyParameter("nickname", nickname);
		  params.addBodyParameter("companyName", companyName);
		  http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() { 
			  @Override 
			  public void onSuccess(ResponseInfo<String> responseInfo) { 
				  int statusCode = responseInfo.statusCode;
				  if(statusCode == 200){
					  String result = responseInfo.result;
					  Log.e("userinfoactivity", result+"");
					  Gson gson = new Gson();
					  MsgBean msgBean = gson.fromJson(result, MsgBean.class);
					  waitDialog.dismiss();
					  if("1".equals(msgBean.code)){
//						  完成操作
						  showShortToastMessage("保存成功");
//			        		发送  登录成功后 让首页重新显示用户信息
				        	 EventBus.getDefault().post(new EvenBusBean("reviseNickname"));
			        	   UserInfoActivity.this.finish();

					  }else {
						  showShortToastMessage(msgBean.msg+"");
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




}