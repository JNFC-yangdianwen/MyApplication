package com.example.administrator.myapplication.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.model.MsgBean;
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
 * 编辑地址
 * @author fengzi
 *
 */
public class UndateAddressActivity extends BaseActivity implements OnClickListener{

	private TextView tv_addr_map;
	private String contact;
	private String mobile;
	private String receive_address;
	private String detailed_address;
	private EditText tv_name;
	private EditText tv_phone;
	private EditText et_detailed_address;
	private String address_id;
	private Button bt_sub_addr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("编辑地址");
		templateButtonRight.setVisibility(View.VISIBLE);
		templateButtonRight.setImageResource(R.drawable.xgdz_del); //删除
		setContentView(R.layout.addr_write);
//		注册eventbus
		 EventBus.getDefault().register(this); 
//		获取上页数据
		Intent intent = getIntent();
		contact = intent.getStringExtra("contact");//姓名
		mobile = intent.getStringExtra("mobile");//电话
		receive_address = intent.getStringExtra("receive_address");//地址
		detailed_address = intent.getStringExtra("detailed_address");//详细地址
		address_id = intent.getStringExtra("address_id");//地址id

		tv_addr_map = (TextView) findViewById(R.id.tv_addr_map);  //地址 
		tv_name = (EditText) findViewById(R.id.tv_name);
		tv_phone = (EditText) findViewById(R.id.tv_phone);
		et_detailed_address = (EditText) findViewById(R.id.et_detailed_address); //详细补充地址
		bt_sub_addr = (Button) findViewById(R.id.bt_sub_addr); //修改确定按钮
		
//		设置地址
		tv_name.setText(contact);
		tv_phone.setText(mobile);
		tv_addr_map.setText(receive_address);
		et_detailed_address.setText(detailed_address);
		
		tv_addr_map.setOnClickListener(this);
		templateButtonRight.setOnClickListener(this);
		bt_sub_addr.setOnClickListener(this);
		templateButtonRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_but_right:
			initDialog();
			break;
			
		case R.id.tv_addr_map:   //跳转到地图
			Intent intent = new Intent(this,PoiListActivity.class);
			startActivity(intent);
			break;
		case R.id.bt_sub_addr:   //修改地址
			reviseAdrr();
			break;
		default:
			break;
		}
	}
	
//	初始化对话框
	public void initDialog(){
		final Dialog dialog = new Dialog(this);
		View dialog_addr_dele = getLayoutInflater().inflate(R.layout.dialog_addr_dele,null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialog_addr_dele);
		View tv_cancel = dialog.findViewById(R.id.tv_cancel);
		View tv_affirm = dialog.findViewById(R.id.tv_affirm);
//		取消
		tv_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
//		删除
		tv_affirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deteAdrr();
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	
	
    
    /***********************网络请求***************************/

/**
 * 修改地址
 */
	  public void reviseAdrr(){
		  receive_address = tv_addr_map.getText().toString();  //地址 
		  detailed_address = et_detailed_address.getText().toString();  //详细补充地址
		  contact = tv_name.getText().toString(); 
		  mobile = tv_phone.getText().toString();  
		  
		  if(contact!=null&&!"".equals(contact) && receive_address!=null&&!"".equals(receive_address) && detailed_address!=null&&!"".equals(detailed_address)){
			  if(!UserUtil.isMobileNO(mobile)){
				  Toast.makeText(UndateAddressActivity.this, "电话号码格式错误", Toast.LENGTH_SHORT).show();
				  return;
			  }
			  waitDialog.show();
		  	String usrId = UserUtil.getUsrId(this);
	        HttpUtils http = new HttpUtils();
	        String url = Contants.reviseAdds; 
	        RequestParams params = new RequestParams(); 
//	         添加请求参数 
	        params.addBodyParameter("user_id", usrId);
	        params.addBodyParameter("contact", contact);
	        params.addBodyParameter("mobile", mobile);
	        params.addBodyParameter("receive_address", receive_address);
	        params.addBodyParameter("detailed_address", detailed_address);
	        params.addBodyParameter("address_id", address_id );
	        http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() { 
			@Override 
	        public void onSuccess(ResponseInfo<String> responseInfo) { 
	        	int statusCode = responseInfo.statusCode;
	        	if(statusCode == 200){
	        		String result = responseInfo.result;
	        		Log.e("UndateAddressActivity", result+"");
	        		Gson gson = new Gson();
	        		MsgBean msgBean = gson.fromJson(result, MsgBean.class);
	        		if("1".equals(msgBean.code)){
	        			waitDialog.dismiss();
//		        		发送
		        	   EventBus.getDefault().post(new EvenBusBean("addr_change")); 
		        	   UndateAddressActivity.this.finish();
	        		}else {
	        			waitDialog.dismiss();
	        			Toast.makeText(UndateAddressActivity.this, msgBean.msg+"", Toast.LENGTH_SHORT).show();
					}
	        	}
	        } 

	        @Override 
	        public void onFailure(HttpException error, String msg) { 
	        	waitDialog.dismiss();
	        	Toast.makeText(UndateAddressActivity.this, "网络加载异常，请稍后再试", Toast.LENGTH_SHORT).show();
	        } 

	        });
	        
	        
			  }else{
				  Toast.makeText(UndateAddressActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
			  }
	    }
 
	  
	  /**
	   * 删除地址
	   */
	 

	  public void deteAdrr(){
		  waitDialog.show();
		  	String usrId = UserUtil.getUsrId(this);
	        HttpUtils http = new HttpUtils();
	        String url = Contants.deleAdds; 
	        RequestParams params = new RequestParams(); 
//	         添加请求参数 
	        params.addBodyParameter("address_id", address_id );
	        http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() { 
			@Override 
	        public void onSuccess(ResponseInfo<String> responseInfo) { 
	        	int statusCode = responseInfo.statusCode;
	        	if(statusCode == 200){
	        		String result = responseInfo.result;
	        		Log.e("UndateAddressActivity", result+"");
	        		Gson gson = new Gson();
	        		MsgBean msgBean = gson.fromJson(result, MsgBean.class);
	        		if("1".equals(msgBean.code)){
	        			waitDialog.dismiss();
//		        		发送
		        	   EventBus.getDefault().post(new EvenBusBean("addr_change")); 
		        	   UndateAddressActivity.this.finish();
	        		}else {
	        			Toast.makeText(UndateAddressActivity.this, msgBean.msg+"", Toast.LENGTH_SHORT).show();
	        			waitDialog.dismiss();
					}
	        	}
	        } 

	        @Override 
	        public void onFailure(HttpException error, String msg) { 
	        	 waitDialog.dismiss();
	        	Toast.makeText(UndateAddressActivity.this, "网络加载异常，请稍后再试", Toast.LENGTH_SHORT).show();
	        } 

	        });
	    }
	  
	  
//    Eventbus回调
	   public void onEventMainThread(EvenBusBean event) {  
		   String msg1 = event.getMsg();
	        if (msg1.equals("addr_map_change")) {
	        	String msg2 = event.getMsg2();  //地址信息
	        	tv_addr_map.setText(msg2);
			}
	    }  
 
}