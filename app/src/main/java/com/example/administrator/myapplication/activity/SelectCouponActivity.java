package com.example.administrator.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
 * 选择优惠劵页面
 * 
 * @author fengzi
 * 
 */
public class SelectCouponActivity extends BaseActivity {

	private ListView yhj;
	private String use_coupon_id;
	protected List<Coupon> data;
	private UserCouponInfoAdapter userCouponInfoAdapter;
	private double sourceMoney;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("选择优惠劵");
		setContentView(R.layout.activity_coupon);
		yhj = (ListView) findViewById(R.id.yhj);
		use_coupon_id = getIntent().getStringExtra("use_coupon_id");
		sourceMoney = getIntent().getDoubleExtra("sourceMoney", 0);
		userCouponInfoAdapter = new UserCouponInfoAdapter();
		initData();

	}

	class UserCouponInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (data == null) {
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

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = getLayoutInflater().inflate(R.layout.coupon_itme_select, null);

			TextView tv_couponMoney = (TextView) view.findViewById(R.id.tv_couponMoney);
			TextView tv_couponName = (TextView) view.findViewById(R.id.tv_couponName);
			TextView tv_usefultime = (TextView) view.findViewById(R.id.tv_usefultime);
			TextView tv_yj = (TextView) view.findViewById(R.id.tv_yj);
			LinearLayout ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
			CheckBox cb_select = (CheckBox) view.findViewById(R.id.cb_select);
			TextView tvManjian = (TextView) view.findViewById(R.id.tv_manjian);
			
			final Coupon coupon = data.get(position);
			tv_couponMoney.setText(coupon.couponMoney); // 金额
			tv_couponName.setText(coupon.couponName); // 标题
			tv_usefultime.setText(coupon.usefultime); // 有效期
			tvManjian.setText("(满"+coupon.condition+"元可用)");
			// status : 1 -- 使用状态(1代表已使用,0代表未使用)
			// coupon_expired : 1 -- 是否过期(1代表已过期,0代表未过期)
			String status = coupon.status;
			String coupon_expired = coupon.coupon_expired;
			double condition = Double.parseDouble(coupon.condition);
			if ("1".equals(status) || "1".equals(coupon_expired)||condition>sourceMoney) {
				ll_bg.setBackgroundResource(R.drawable.wdyhq_coupons_used);
				tv_couponMoney.setTextColor(Color.parseColor("#BEBEBD"));
				tvManjian.setTextColor(Color.parseColor("#BEBEBD"));
				tv_couponName.setTextColor(getResources().getColor(R.color.text_gray));
				tv_yj.setTextColor(getResources().getColor(R.color.text_gray));
				cb_select.setVisibility(View.GONE);
			}
			// 选择过的设为选择 比较是的优惠券的唯一id 数据库的自增id
			if (use_coupon_id != null && use_coupon_id.equals(coupon.usecouponId)) {
				cb_select.setChecked(true);
			} else {
				cb_select.setChecked(false);
			}
			// 选择按钮监听
			cb_select.setOnClickListener(new OnClickListener() {

				private String couponMoney;

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					if (!((CheckBox) v).isChecked()) {
						use_coupon_id = "0";
						couponMoney = "0";
						userCouponInfoAdapter.notifyDataSetChanged();
					} else {
						// 注意 这里使用的是数据库自增id 这个是唯一的
						// use_coupon_id = coupon.coupon_id;
						use_coupon_id = coupon.usecouponId;
						couponMoney = coupon.couponMoney;
						userCouponInfoAdapter.notifyDataSetChanged();
					}
					intent.putExtra("use_coupon_id", use_coupon_id);
					intent.putExtra("couponMoney", couponMoney);
					setResult(RESULT_OK, intent);
					finish();

				}
			});
			return view;

		}

	}

	/**
	 * 网络数据获取
	 */
	public void initData() {
		waitDialog.show();
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.couponList;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("user_id", usrId);
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("UserCouponActivity", result + "");
					Gson gson = new Gson();
					CouponBean couponBean = gson.fromJson(result, CouponBean.class);
					if ("1".equals(couponBean.code)) {
						for (int i = 0; i < couponBean.data.size(); i++) {
							if ("1".equals(couponBean.data.get(i).status)||couponBean.data.get(i).coupon_expired.equals("1")) {
								couponBean.data.remove(i);
							}
						}
						data = couponBean.data;
						waitDialog.dismiss();
						yhj.setAdapter(userCouponInfoAdapter);
					} else {
						waitDialog.dismiss();
						Log.i("SelectCouponActivity", couponBean.msg + "");
						// Toast.makeText(SelectCouponActivity.this,
						// couponBean.msg+"", Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				Toast.makeText(SelectCouponActivity.this, "网络加载异常，请稍后再试", Toast.LENGTH_SHORT).show();
			}

		});
	}
}