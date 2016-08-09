package com.example.administrator.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.AddrsBean;
import com.example.administrator.myapplication.model.AddrsBean.Addr;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.utils.UserUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
/**
 * 地址选择
 * @author fengzi
 *注意跳到该类时需要 判断时从地址管理过来的 还是  订单选择地址过来的
 */
public class SelectAddressActivity extends BaseActivity implements OnClickListener{

	private ListView lv_selectaddr;
	private View bt_new_addr;
	private SelectAddrAdapter selectAddrAdapter;
	protected List<Addr> data;
	private String orderMarking;
	private String address_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("选择收货地址");
		
		setContentView(R.layout.activity_selectaddr);
//		注册eventbus
		 EventBus.getDefault().register(this); 
		 lv_selectaddr = (ListView) findViewById(R.id.lv_selectaddr);
		 bt_new_addr =  findViewById(R.id.bt_new_addr);
//		 订单标示 如果是从订单页面过来的 有此标示 否则是null  
		 orderMarking = getIntent().getStringExtra("orderMarking");
		 address_id = getIntent().getStringExtra("address_id");
		 initData();
		 selectAddrAdapter = new SelectAddrAdapter();
		 bt_new_addr.setOnClickListener(this);
		 
//		 先判断有没有订单标示（没有标示从首页来的）    有标示则做条目事件监听（从订单来的）  将选择的条目返回给订单
		 if("orderMarking".equals(orderMarking)){
			 lv_selectaddr.setOnItemClickListener(new SelectOnItemClickListener());
		 }
		 


	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_new_addr:
			Intent intent = new Intent(this,AddAddressActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	
    class SelectAddrAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(data==null){
				return 0;
			}
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View order_itme = getLayoutInflater().inflate(R.layout.selectaddr_itme, null);
			View ib_go_selectaddr = order_itme.findViewById(R.id.ib_go_selectaddr);
			TextView tv_name = (TextView) order_itme.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) order_itme.findViewById(R.id.tv_phone);
			TextView tv_addr = (TextView) order_itme.findViewById(R.id.tv_addr);
			ImageView iv_select = (ImageView) order_itme.findViewById(R.id.iv_select);
			LinearLayout ll_addrgon = (LinearLayout) order_itme.findViewById(R.id.ll_addrgon);
			if(orderMarking==null||!"orderMarking".equals(orderMarking)){
				ll_addrgon.setVisibility(View.GONE);
			}
			
			iv_select.setVisibility(View.VISIBLE);
			Addr addr = data.get(position);
			tv_name.setText(addr.contact);
			tv_phone.setText(addr.mobile);
			tv_addr.setText(addr.receive_address+"  "+addr.detailed_address);
			if(address_id!=null&&addr.id!=null&&address_id.equals(addr.id)){
				iv_select.setImageResource(R.drawable.qrdd_check);
			}else {
				iv_select.setImageResource(R.drawable.qrdd_nocheck);
			}
			
			ib_go_selectaddr.setOnClickListener(new UpdateAddrOnClickListener(position));
			return order_itme;
		}
    	
    }
    
//    编辑监听器
    class UpdateAddrOnClickListener implements OnClickListener{
    	int position;
    	public UpdateAddrOnClickListener(int position){
    		this.position = position;
    	}
    	@Override
    	public void onClick(View v) {
    		Addr addr = data.get(position);

    		Intent intent = new Intent(SelectAddressActivity.this,UndateAddressActivity.class);
    		intent.putExtra("contact", addr.contact);//姓名
    		intent.putExtra("mobile", addr.mobile);//电话
    		intent.putExtra("receive_address", addr.receive_address);//地址
    		intent.putExtra("detailed_address", addr.detailed_address);//详细地址
    		intent.putExtra("address_id", addr.id );//地址id
    		SelectAddressActivity.this.startActivity(intent);
    		
    	}

    }
    
    
    
    class SelectOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
//			ImageView iv_select = (ImageView) arg1.findViewById(R.id.iv_select);
//			iv_select.setImageResource(R.drawable.qrdd_check);
 
 
			
			Addr addr = data.get(arg2);
			address_id = addr.id;
	   		selectAddrAdapter.notifyDataSetChanged();
    		ImageView iv_select = (ImageView) arg1.findViewById(R.id.iv_select);
			iv_select.setImageResource(R.drawable.qrdd_check);
			
			Intent intent = new Intent();
			intent.putExtra("contact", addr.contact);  //姓名
			intent.putExtra("address_id", addr.id);  //id
			intent.putExtra("mobile", addr.mobile);  //电话
			intent.putExtra("receive_address", addr.receive_address);  //地址
			intent.putExtra("detailed_address", addr.detailed_address);  //详细地址
			setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
			finish();//此处一定要调用finish()方法
		}
    	
    }



    
    /***********************网络请求***************************/

	  public void initData(){
		  waitDialog.show();
		  	String usrId = UserUtil.getUsrId(this);
	        HttpUtils http = new HttpUtils();
	        String url = Contants.getAdds; 
	        RequestParams params = new RequestParams(); 
//	         添加请求参数 
	        params.addBodyParameter("user_id", usrId);
	        http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() { 
			@Override 
	        public void onSuccess(ResponseInfo<String> responseInfo) { 
	        	int statusCode = responseInfo.statusCode;
	        	if(statusCode == 200){
	        		String result = responseInfo.result;
	        		Log.e("SelectAddressActivity", result+"");
	        		Gson gson = new Gson();
	        		AddrsBean AddrsBean = gson.fromJson(result, AddrsBean.class);
	        		waitDialog.dismiss();
	        		if("1".equals(AddrsBean.code)){
	        			 data = AddrsBean.data;
		        	     Collections.reverse(data);
	        			 lv_selectaddr.setAdapter(selectAddrAdapter);
	        		}else {
	        			Toast.makeText(SelectAddressActivity.this, "暂无可选地址", Toast.LENGTH_SHORT).show();
					}
	        		 
	        	}
	        } 


	        @Override 
	        public void onFailure(HttpException error, String msg) { 
	        	waitDialog.dismiss();
	        	Toast.makeText(SelectAddressActivity.this, "网络加载异常，请稍后再试", Toast.LENGTH_SHORT).show();
	        } 

	        }); 
	    }
	  
	  
//    Eventbus回调
	   public void onEventMainThread(EvenBusBean event) {  
		   
	        String msg1 = event.getMsg();
	        if("addr_change".equals(msg1)){
	        	initData();
	        }
	    }  
    
}