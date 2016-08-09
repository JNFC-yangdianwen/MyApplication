package com.example.administrator.myapplication.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.CouponBean;
import com.example.administrator.myapplication.model.CouponBean.Coupon;
import com.example.administrator.myapplication.utils.UserUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import java.util.List;
/**
 * 用户优惠劵列表页面
 * @author fengzi
 *
 */
public class UserCouponActivity extends BaseActivity{

	private ListView yhj;
	protected List<Coupon> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("优惠劵");
		setContentView(R.layout.activity_coupon);
		 yhj = (ListView) findViewById(R.id.yhj);
		
		 
		 initData();
	}
	
	
	
	class UserCouponInfoAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(data==null){
				return 0 ;
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

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = getLayoutInflater().inflate(R.layout.coupon_itme, null);
			TextView tv_couponMoney = (TextView) view.findViewById(R.id.tv_couponMoney);
			TextView tv_couponName = (TextView) view.findViewById(R.id.tv_couponName);
			TextView tv_usefultime = (TextView) view.findViewById(R.id.tv_usefultime);
			TextView tvManjian = (TextView) view.findViewById(R.id.tv_manjian);
			TextView tv_yj = (TextView) view.findViewById(R.id.tv_yj);
			LinearLayout ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
			
			Coupon coupon = data.get(position);
			tv_couponMoney.setText(coupon.couponMoney); //金额
			tv_couponName.setText(coupon.couponName);  //标题
			tv_usefultime.setText(coupon.usefultime); //有效期
			tvManjian.setText("(满"+coupon.condition+"元可用)");
			
//			status         : 1         -- 使用状态(1代表已使用,0代表未使用)
//	        coupon_expired : 1         -- 是否过期(1代表已过期,0代表未过期)
			String status = coupon.status;
			String coupon_expired = coupon.coupon_expired;
			if("1".equals(status)||"1".equals(coupon_expired)){
				ll_bg.setBackgroundResource(R.drawable.wdyhq_coupons_used);
				tv_couponMoney.setTextColor(Color.parseColor("#BEBEBD"));
				tvManjian.setTextColor(Color.parseColor("#BEBEBD"));
				tv_couponName.setTextColor(getResources().getColor(R.color.text_gray));
				tv_yj.setTextColor(getResources().getColor(R.color.text_gray));
			}
			return view;
		}
		
	}
	
	
	
	/**
	 * 网络数据获取
	 */
		  public void initData(){
				  waitDialog.show();
			  	String usrId = UserUtil.getUsrId(this);
		        HttpUtils http = new HttpUtils();
		        String url = Contants.couponList; 
		        RequestParams params = new RequestParams(); 
//		         添加请求参数 
		        params.addBodyParameter("user_id", usrId);
		        http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() { 
				@Override 
		        public void onSuccess(ResponseInfo<String> responseInfo) { 
		        	int statusCode = responseInfo.statusCode;
		        	if(statusCode == 200){
		        		String result = responseInfo.result;
		        		Log.e("UserCouponActivity", result+"");
		        		Gson gson = new Gson();
		        		CouponBean couponBean = gson.fromJson(result, CouponBean.class);
		        		if("1".equals(couponBean.code)){
		        			 data = couponBean.data;
		        			waitDialog.dismiss();
		        			 yhj.setAdapter(new UserCouponInfoAdapter());
		        		}else {
		        			waitDialog.dismiss();
//		        			Toast.makeText(UserCouponActivity.this, couponBean.msg+"", Toast.LENGTH_SHORT).show();
						}
		        	}
		        } 

		        @Override 
		        public void onFailure(HttpException error, String msg) { 
		        	waitDialog.dismiss();
		        	Toast.makeText(UserCouponActivity.this, "网络加载异常，请稍后再试", Toast.LENGTH_SHORT).show();
		        } 

		        });
		    }
	
}