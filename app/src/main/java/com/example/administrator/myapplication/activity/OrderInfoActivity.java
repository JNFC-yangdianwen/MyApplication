package com.example.administrator.myapplication.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.BfApplication;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.model.FoodCarModel;
import com.example.administrator.myapplication.model.RepeatOrderBean;
import com.example.administrator.myapplication.model.RepeatOrderBean.RepeatOrderinfo;
import com.example.administrator.myapplication.model.RepeatOrderBean.RepeatOrderinfo.RepeatOrder;
import com.example.administrator.myapplication.utils.UserUtil;
import com.example.administrator.myapplication.view.CustomListView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import de.greenrobot.event.EventBus;
/**
 * 订单详情信息页面
 * @author fengzi
 *
 */
public class OrderInfoActivity extends BaseActivity implements OnClickListener{

	private CustomListView clv_name;
	private TextView tv_coupon_money;
	private TextView tv_ordernum;
	private TextView tv_order_time;
	private TextView tv_pay_info;
	private TextView tv_receiver;
	private TextView tv_mobile;
	private TextView tv_address;
	protected List<RepeatOrder> order_assort;
	private Button bt_share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("订单详情");
		setContentView(R.layout.activity_orderinfo);
		templateButtonRight.setVisibility(View.VISIBLE);
		templateButtonRight.setImageResource(R.drawable.wddd_tel);
		 clv_name = (CustomListView) findViewById(R.id.clv_name);
		 tv_coupon_money = (TextView) findViewById(R.id.tv_coupon_money);
		 tv_ordernum = (TextView) findViewById(R.id.tv_ordernum);
		 tv_order_time = (TextView) findViewById(R.id.tv_order_time);
		 tv_pay_info = (TextView) findViewById(R.id.tv_pay_info);
		 tv_receiver = (TextView) findViewById(R.id.tv_receiver);
		 tv_mobile = (TextView) findViewById(R.id.tv_mobile);
		 tv_address = (TextView) findViewById(R.id.tv_address);
		 bt_share = (Button) findViewById(R.id.bt_share);
		 String order_id = getIntent().getStringExtra("order_id");

		 initData(order_id);
		 bt_share.setOnClickListener(this);
		 templateButtonRight.setOnClickListener(this);

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_share:
			 zlyd();
			break;
		case R.id.title_but_right:
			AlertDialog.Builder bulider = new AlertDialog.Builder(this);
			bulider.setMessage("拨打"+getString(R.string.phone)+"?");
			bulider.setPositiveButton("拨打", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent5 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+getString(R.string.phone)));
					startActivity(intent5);
				}
			});
			bulider.setNegativeButton("取消", null);
			bulider.show();
			break;
		default:
			break;
		}
		
	}
	
	public void zlyd(){
		  
		if(order_assort==null){
			showShortToastMessage("网络异常，请稍后再试~");
			return;
		}
		  BfApplication.carMap.clear();
		  for (RepeatOrder repeatOrder : order_assort) {
			  /******************************注意 这里模型类型改了必须改接收类型************************************************************/
			FoodCarModel foodCarModel = new FoodCarModel();
			int number = Integer.parseInt(repeatOrder.number); //总代数
			double sum_pounds = repeatOrder.sum_pounds; //总斤数
			double sum_money = Double.parseDouble(repeatOrder.sum_money); //总金额
			
			foodCarModel.title = repeatOrder.title;   //名称
			foodCarModel.id = repeatOrder.goods_id;   //id
			foodCarModel.daiSumNum = number; //总代数
			foodCarModel.each_bag_money = sum_money/number;   //每袋价格
			foodCarModel.pounds = sum_pounds/number;   //每袋价格
			BfApplication.carMap.put(repeatOrder.goods_id, foodCarModel);
//			成功后 改变购物车 跳转支付页面
			EventBus.getDefault().post(new EvenBusBean("search_change")); 
			Intent intent = new Intent(OrderInfoActivity.this,OrderConfirmActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			OrderInfoActivity.this.startActivity(intent);
			OrderInfoActivity.this.finish();
		}
	}
	
	
	class OrderInfoAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(order_assort==null){
			return 0;
			}
			return order_assort.size();
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
			View view = getLayoutInflater().inflate(R.layout.order_info_itme, null);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
			TextView tv_price = (TextView) view.findViewById(R.id.tv_price);
			RepeatOrder repeatOrder = order_assort.get(position);
			
			tv_title.setText(repeatOrder.title);
			
			tv_num.setText("*"+repeatOrder.number);//+" (共"+repeatOrder.sum_pounds+"斤)"
			tv_price.setText("￥"+FoodCarModel.convertDouble(Double.parseDouble(repeatOrder.sum_money)));
			return view;
		}
		
	}



	  /**
	   * 网络数据调用
	   */
	  public void initData(String order_id){
		  waitDialog.show();
		  String usrId = UserUtil.getUsrId(this);
		  HttpUtils http = new HttpUtils();
		  String url = Contants.orderInof; 
		  RequestParams params = new RequestParams(); 
//	         添加请求参数 
		  params.addBodyParameter("order_id", order_id);
		  http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() { 
			  @Override 
			  public void onSuccess(ResponseInfo<String> responseInfo) { 
				  int statusCode = responseInfo.statusCode;
				  if(statusCode == 200){
					  String result = responseInfo.result;
					  Log.e("OrderListActivity", result+"");
					  Gson gson = new Gson();
					  RepeatOrderBean repeatOrderBean = gson.fromJson(result, RepeatOrderBean.class);
					  waitDialog.dismiss();
					  if("1".equals(repeatOrderBean.code)){
						  RepeatOrderinfo data = repeatOrderBean.data;
						  order_assort = repeatOrderBean.data.order_assort;
						  tv_coupon_money.setText("-￥"+ FoodCarModel.convertDouble(Double.parseDouble(data.coupon_money)));
						  tv_ordernum.setText(data.ordernum);
						  tv_order_time.setText(data.order_time);
						  tv_pay_info.setText(data.pay_info);
						  tv_receiver.setText(data.receiver);
						  tv_mobile.setText(data.mobile);
						  tv_address.setText(data.address);
						  clv_name.setAdapter(new OrderInfoAdapter());
						  

//						  收货完成操作
					  }else {
						  Toast.makeText(OrderInfoActivity.this, repeatOrderBean.msg+"", Toast.LENGTH_SHORT).show();
					  }
					  
				  }
			  } 
			  
			  
			  @Override 
			  public void onFailure(HttpException error, String msg) { 
				  waitDialog.dismiss();
				  Toast.makeText(OrderInfoActivity.this, "网络加载异常，请稍后再试", Toast.LENGTH_SHORT).show();
			  } 
			  
		  }); 
	  }


}