package com.example.administrator.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
 * 新增地址
 * @author fengzi
 *
 */
public class AddAddressActivity extends BaseActivity implements OnClickListener{

	private TextView tv_addr_map;
	private String receive_address;
	private EditText tv_name;
	private EditText tv_phone;
	private EditText et_detailed_address;
	private String detailed_address;
	private String contact;
	private String mobile;
	private View bt_sub_addr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("新增地址");
		setContentView(R.layout.addr_write);
//		注册eventbus
		 EventBus.getDefault().register(this); 
		
		tv_addr_map = (TextView) findViewById(R.id.tv_addr_map);  //地址 
		tv_name = (EditText) findViewById(R.id.tv_name);//姓名
		tv_phone = (EditText) findViewById(R.id.tv_phone);//电话
		et_detailed_address = (EditText) findViewById(R.id.et_detailed_address); //详细补充地址
		bt_sub_addr =  findViewById(R.id.bt_sub_addr); //确定
		 
		 tv_addr_map.setOnClickListener(this);
		 bt_sub_addr.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_addr_map:   //跳转到地图
			Intent intent = new Intent(this,PoiListActivity.class);
			startActivity(intent);
			break;
		case R.id.bt_sub_addr:   //新建地址
			newAdrr();
			break;

		default:
			break;
		}
	}
	
	
	
    /***********************网络请求***************************/

/**
 * 修改地址
 */
	  public void newAdrr(){
		  receive_address = tv_addr_map.getText().toString();  //地址 
		  detailed_address = et_detailed_address.getText().toString();  //详细补充地址
		  contact = tv_name.getText().toString(); 
		  mobile = tv_phone.getText().toString();  
		  
		  
//		  if(contact!=null&&!"".equals(contact) && receive_address!=null&&!"".equals(receive_address) && detailed_address!=null&&!"".equals(detailed_address)){
			  if(contact==null||"".equals(contact)){
				  Toast.makeText(AddAddressActivity.this, "联系人不能为空", Toast.LENGTH_SHORT).show();
				  return;
			  }
			  if(receive_address==null||"".equals(receive_address)){
				  Toast.makeText(AddAddressActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
				  return;
			  }
			  if(detailed_address==null||"".equals(detailed_address)){
				  Toast.makeText(AddAddressActivity.this, "详细地址不能为空", Toast.LENGTH_SHORT).show();
				  return;
			  }
			  
			  if(!UserUtil.isMobileNO(mobile)){
				  Toast.makeText(AddAddressActivity.this, "电话号码格式错误", Toast.LENGTH_SHORT).show();
				  return;
			  }
			  waitDialog.show();
		  	String usrId = UserUtil.getUsrId(this);
	        HttpUtils http = new HttpUtils();
	        String url = Contants.newAddr; 
	        RequestParams params = new RequestParams(); 
//	         添加请求参数 
	        params.addBodyParameter("user_id", usrId);
	        params.addBodyParameter("contact", contact);
	        params.addBodyParameter("mobile", mobile);
	        params.addBodyParameter("receive_address", receive_address);
	        params.addBodyParameter("detailed_address", detailed_address);
	        http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() { 
			@Override 
	        public void onSuccess(ResponseInfo<String> responseInfo) { 
	        	int statusCode = responseInfo.statusCode;
	        	if(statusCode == 200){
	        		String result = responseInfo.result;
	        		Log.e("AddAddressActivity", result+"");
	        		Gson gson = new Gson();
	        		MsgBean msgBean = gson.fromJson(result, MsgBean.class);
	        		if("1".equals(msgBean.code)){
	        			waitDialog.dismiss();
//		        		发送
		        	   EventBus.getDefault().post(new EvenBusBean("addr_change")); 
		        	   AddAddressActivity.this.finish();
	        		}else {
	        			waitDialog.dismiss();
	        			Toast.makeText(AddAddressActivity.this, msgBean.msg+"", Toast.LENGTH_SHORT).show();
					}
	        	}
	        } 

	        @Override 
	        public void onFailure(HttpException error, String msg) { 
	        	waitDialog.dismiss();
	        	Toast.makeText(AddAddressActivity.this, "网络加载异常，请稍后再试", Toast.LENGTH_SHORT).show();
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