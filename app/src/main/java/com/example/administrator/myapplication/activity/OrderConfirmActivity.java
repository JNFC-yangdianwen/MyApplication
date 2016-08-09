package com.example.administrator.myapplication.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.alipay.PayDemoActivity;
import com.example.administrator.myapplication.globle.BfApplication;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.CouponBean;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.model.FoodCarModel;
import com.example.administrator.myapplication.model.OrderBean;
import com.example.administrator.myapplication.model.SendTimeBean;
import com.example.administrator.myapplication.model.SendTimeBean.SendTime;
import com.example.administrator.myapplication.model.SendTimeBean.SendTime.TimeInfo;
import com.example.administrator.myapplication.utils.UserUtil;
import com.example.administrator.myapplication.view.CustomListView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * 确认订单信息类
 * 
 * @author fengzi
 * 
 */
@SuppressLint("NewApi")
public class OrderConfirmActivity extends BaseActivity implements
		OnClickListener {

	private String[] mDateDisplayValues = null;
	private CustomListView clv_name;
	private View ib_go_selectaddr;
	private View ll_explain;
	private View ll_djj;
	private View setect_time;
	private int windowHeight;
	private int windowWidth;
	private Dialog mDialog;
	private NumberPicker mDateSpinner;
	private Button dialog_ensure_button_cancel;
	private Button dialog_ensure_button_sure;
	private View ll_pay;
	private TextView tv_name;
	private TextView tv_addr;

	private String use_coupon_id = "0"; // 优惠券id 当为0时 说明无id
	private String couponMoney = "0"; // 选择的优惠金额
	private String beizhu; // 备注内容
	double money = 0; // 总价 注意需要减去优惠券的
	private String reach_time; // 送达时间
	private String address; // 地址
	private int pay_status = 1; // 1在线支付(默认) 2货到付款
	private String mobile;
	private String contact;

	private String dal_reach_time; // 送达时间 从对话框中记录的获取派送时间

	private double sourceMoney = 0; // 不变动的总价 未减去优惠劵的总价

	private TextView tv_coupon;
	private View ll_coupon;
	private TextView tv_text;
	private RadioButton rb_zxpay;
	private RadioButton rb_xxpay;
	private ArrayList<FoodCarModel> arrayList; // 保存商品列表
	private TextView tv_money;

	protected ArrayList<String> timeList; // 保存派送时间列表
	private Dialog dialog;
	private TextView tv_send_time;
	private View ll_addr;

	private String address_id; // 从地址选择页获取到的地址id

	private int couponSize;// 抵价券的数量
	private IWXAPI api;
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private String order_info = "";

	private OrderBean msgBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("确认订单");
		setContentView(R.layout.activity_orderconfirm);
		// 注册eventbus
		EventBus.getDefault().register(this);

		clv_name = (CustomListView) findViewById(R.id.clv_name);
		ll_addr = findViewById(R.id.ll_addr);
		ib_go_selectaddr = findViewById(R.id.ib_go_selectaddr);
		ll_explain = findViewById(R.id.ll_explain);
		ll_djj = findViewById(R.id.ll_djj);
		setect_time = findViewById(R.id.setect_time);
		ll_pay = findViewById(R.id.ll_pay);
		// 地址
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_addr = (TextView) findViewById(R.id.tv_addr);
		// 优惠劵
		tv_coupon = (TextView) findViewById(R.id.tv_coupon);
		ll_coupon = findViewById(R.id.ll_coupon);
		tvDjq = (TextView) findViewById(R.id.tvDjq);
		// 备注
		tv_text = (TextView) findViewById(R.id.tv_text);
		// 支付方式选择
		rb_zxpay = (RadioButton) findViewById(R.id.rb_zxpay); // 在线支付
		rb_xxpay = (RadioButton) findViewById(R.id.rb_xxpay); // 货到付款
		// 金额
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_send_time = (TextView) findViewById(R.id.tv_send_time); // 派送时间显示text

		tvYunfei = (TextView) findViewById(R.id.tvYunfei);
		// 初始化派送时间
		initTimeData();
		// 初始化商品列表
		initGoods();

		// ib_go_selectaddr.setOnClickListener(this); //选择地址
		ll_addr.setOnClickListener(this); // 选择地址
		ll_explain.setOnClickListener(this); // 添加备注
		ll_djj.setOnClickListener(this); // 选择抵价券
		setect_time.setOnClickListener(this); // 选择配送时间

		rb_zxpay.setOnClickListener(this); // 选择支付方式
		rb_xxpay.setOnClickListener(this);
		ll_pay.setOnClickListener(this); // 选择使用支付厂家

		ScrollView view = (ScrollView) findViewById(R.id.slview);
		view.smoothScrollTo(0, 20);

		// 获取抵用券的数量
		getMyCouponListSize();
		regToWx();
	}

	/**
	 * 
	 * @author czz
	 * @createdate 2015年11月27日 上午9:42:46
	 * @Description: (注册微信)
	 * 
	 */
	private void regToWx() {
		api = WXAPIFactory.createWXAPI(this, Contants.APP_ID);
		api.registerApp(Contants.APP_ID);
	}

	/**
	 * 获取抵用券的数量
	 */
	private void getMyCouponListSize() {
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
					CouponBean couponBean = gson.fromJson(result,
							CouponBean.class);
					if ("1".equals(couponBean.code)) {
						couponSize = couponBean.data.size();
						if (couponBean.data.size() == 0) {
							tvDjq.setText("无可用优惠券");
						} else {
							tvDjq.setText("请选择优惠券");
						}
					} else {
						couponSize = 0;
						tvDjq.setText("无可用优惠券");
						Log.i("SelectCouponActivity", couponBean.msg + "");
						// Toast.makeText(SelectCouponActivity.this,
						// couponBean.msg+"", Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
			}

		});
	}

	// 初始化订单列表及总金额
	private void initGoods() {

		// 获取数据
		arrayList = new ArrayList<FoodCarModel>();
		Map<String, FoodCarModel> carMap = BfApplication.carMap;
		Set<String> keySetCar = carMap.keySet();

		for (String id : keySetCar) {
			arrayList.add(carMap.get(id));
			money = money + carMap.get(id).daiSumNum
					* carMap.get(id).each_bag_money;
		}
		clv_name.setAdapter(new OrderInfoAdapter());
		// if (money<=68) {
		// tvYunfei.setText("￥"+FoodCarModel.convertDouble(6));
		// money+=6;
		// }else if(money>68&&money<=158){
		// tvYunfei.setText("￥"+FoodCarModel.convertDouble(4));
		// money+=4;
		// }
		// else if(money>158&&money<=218){
		// tvYunfei.setText("￥"+FoodCarModel.convertDouble(2));
		// money+=2;
		// }
		// else{
		tvYunfei.setText("免运费");
		// }
		sourceMoney = money;
		initSumMoney();
	}

	// 设置总价
	private void initSumMoney() {
		// 先减去优惠劵的钱
		if (couponMoney != null && !"".equals(couponMoney)) {
			double parseDouble = Double.parseDouble(couponMoney);
			money = money - parseDouble;
			if (money < 0) {
				money = 0;
			}
			tv_money.setText("￥" + FoodCarModel.convertDouble(money));
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_addr: // 选择地址
			Intent intent = new Intent(this, SelectAddressActivity.class);
			// 携带订单标示到 地址页面去 以便区分
			intent.putExtra("orderMarking", "orderMarking");
			intent.putExtra("address_id", address_id);
			startActivityForResult(intent, 0);
			break;
		case R.id.ll_explain: // 添加备注
			Intent intent1 = new Intent(this, AddExplainActivity.class);
			String bztext = tv_text.getText().toString() + "";
			intent1.putExtra("bztext", bztext);
			startActivity(intent1);
			break;
		case R.id.ll_djj: // 选择抵价券
			// 根据支付方式判断是否能使用优惠券
			if (pay_status == 1) {
				if (couponSize != 0) {
					Intent intent2 = new Intent(this,
							SelectCouponActivity.class);
					intent2.putExtra("sourceMoney", sourceMoney);
					intent2.putExtra("use_coupon_id", use_coupon_id);
					startActivityForResult(intent2, 1);
				}
			} else {
				Toast.makeText(this, "亲，在线支付才可以使用优惠券哟~", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		case R.id.setect_time: // 选择配送时间
			if (mDateDisplayValues != null) {
				initTime();
			} else {
				Toast.makeText(this, "网络异常，请稍后再试~", Toast.LENGTH_SHORT).show();
			}

			break;
		// 支付方式选择
		case R.id.rb_zxpay: // 在线支付
			pay_status = 1;
			ll_djj.setVisibility(View.VISIBLE);
			rb_zxpay.setChecked(true);
			rb_xxpay.setChecked(false);
			break;
		case R.id.rb_xxpay: // 货到付款
			pay_status = 2;
			rb_zxpay.setChecked(false);
			rb_xxpay.setChecked(true);
			// 改变优惠券记录 不使用优惠劵
			use_coupon_id = "0"; // 优惠券id 当为0时 说明无id
			couponMoney = "0"; // 选择的优惠金额
			ll_coupon.setVisibility(View.GONE);
			ll_djj.setVisibility(View.GONE);
			// 先恢复原价 改变总金额变成原价
			money = sourceMoney;
			initSumMoney();

			break;

		case R.id.ll_pay: // 选择支付

			// 提交信息 获取订单号
			getOrder();
			break;

		default:
			break;
		}
	}

	// 显示选择的宝贝
	class OrderInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (arrayList == null) {
				return 0;
			}
			return arrayList.size();
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
			View view = getLayoutInflater().inflate(R.layout.order_info_itme,
					null);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
			TextView tv_price = (TextView) view.findViewById(R.id.tv_price);

			FoodCarModel foodCarModel = arrayList.get(position);
			tv_title.setText(foodCarModel.title);

			int daiSumNum = foodCarModel.daiSumNum; // 几袋
			double pounds = foodCarModel.pounds; // 一袋几斤
			double each_bag_money = foodCarModel.each_bag_money; // 每袋单击

			tv_num.setText("*" + daiSumNum);
			tv_price.setText(foodCarModel.convertDouble(daiSumNum
					* each_bag_money)
					+ "");

			return view;
		}
	}

	// 选择配送时间
	public void initTime() {
		dialog = new Dialog(this, R.style.dateDialogStyle);
		View dialog_addr_dele = getLayoutInflater().inflate(
				R.layout.datedialog, null);
		// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialog_addr_dele);
		initNumberPicker(dialog_addr_dele);
		View cancel = dialog_addr_dele
				.findViewById(R.id.dialog_ensure_button_cancel);
		View sure = dialog_addr_dele
				.findViewById(R.id.dialog_ensure_button_sure);
		cancel.setOnClickListener(new CancelSureOnClickListener());
		sure.setOnClickListener(new CancelSureOnClickListener());

		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		windowHeight = display.getHeight();
		windowWidth = display.getWidth();

		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = windowWidth;
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 派送时间 确定按钮
	class CancelSureOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_ensure_button_cancel:
				dialog.dismiss();
				break;
			case R.id.dialog_ensure_button_sure:
				// 确定选择时间
				reach_time = dal_reach_time;
				tv_send_time.setText(reach_time + "");
				dialog.dismiss();
				break;

			default:
				break;
			}
		}

	}

	// 初始化 时间选择器
	private void initNumberPicker(View dialog_addr_dele) {
		// TODO Auto-generated method stub

		// 数据在进入页面时就已经获取好了
		mDateSpinner = (NumberPicker) dialog_addr_dele
				.findViewById(R.id.np_date);
		dialog_ensure_button_cancel = (Button) dialog_addr_dele
				.findViewById(R.id.dialog_ensure_button_cancel);
		dialog_ensure_button_sure = (Button) dialog_addr_dele
				.findViewById(R.id.dialog_ensure_button_sure);

		updateHourControl(0);
		// 默认使用第一个数据
		String time = mDateDisplayValues[0];
		String replace = time.replace("   ", "_");
		dal_reach_time = replace;
		mDateSpinner.setOnValueChangedListener(mOnDateChangedListener);

	}

	// 数据改变时监听
	private OnValueChangeListener mOnDateChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			// newVal 显示的滑动的角标 oldVal 老角标 使用newVal来记录即可
			// 选择后记录数据 默认选择第0的数据
			// Log.e("OrderConfirmActivity", oldVal+"----"+newVal);
			// 记录选择的时间
			String time = mDateDisplayValues[newVal];
			String replace = time.replace("   ", "_");
			dal_reach_time = replace;
			Log.e("OrderConfirmActivity", dal_reach_time + "----" + newVal);
		}
	};
	private TextView tvDjq;
	private TextView tvYunfei;

	private void updateHourControl(int numVal) {
		mDateSpinner.setMinValue(0); // 最大和最小
		mDateSpinner.setMaxValue(mDateDisplayValues.length - 1);
		mDateSpinner.setDisplayedValues(mDateDisplayValues); // 设置数据
		mDateSpinner.setValue(numVal); // 设置默认显示条目
		mDateSpinner.invalidate();
	}

	// 选择厂家支付
	public void initpay(final String orderId) {
		mDialog = new Dialog(this, R.style.customDialog);
		mDialog.setContentView(R.layout.dialog_layout_pay);
		// 取消对话框
		View tv_cancel = mDialog.findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		View llAlipay = mDialog.findViewById(R.id.ll_alipay);
		View llWechatpay = mDialog.findViewById(R.id.ll_wechatpay);
		llAlipay.setOnClickListener(new OnClickListener() { // 支付宝支付
			@Override
			public void onClick(View arg0) {
				new PayDemoActivity(OrderConfirmActivity.this, "精菜",
						order_info, money + "", orderId).check();
			}
		});
		llWechatpay.setOnClickListener(new OnClickListener() {// 微信支付
					@Override
					public void onClick(View arg0) {
						doWxPayById(orderId, money + "");
					}
				});
		// 获取屏蔽大小
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		windowHeight = display.getHeight();
		windowWidth = display.getWidth();

		Window dialogWindow = mDialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM); // 设置显示位置
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = windowWidth;
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);

		mDialog.show();
	}

	/**
	 * 派送时间*********************** 网络数据获取***************
	 */
	public void initTimeData() {
		waitDialog.show();
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.sendTimeList;
		// 添加请求参数
		http.send(HttpMethod.POST, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("OrderConfirmActivity", result + "");
					Gson gson = new Gson();
					SendTimeBean sendTimeBean = gson.fromJson(result,
							SendTimeBean.class);
					if ("1".equals(sendTimeBean.code)) {

						timeList = new ArrayList<String>();
						List<SendTime> data = sendTimeBean.data;
						for (int i = 0; i < data.size(); i++) {
							SendTime sendTime = data.get(i);
							List<TimeInfo> duration_list = sendTime.duration_list;
							// String today_week = sendTime.today_week;
							String current_date = sendTime.current_date;
							for (int j = 0; j < duration_list.size(); j++) {
								TimeInfo timeInfo = duration_list.get(j);
								timeList.add(current_date + "   "
										+ timeInfo.starttime + "-"
										+ timeInfo.endtime);
							}
						}

						// 填充时间数据
						mDateDisplayValues = new String[timeList.size()];
						for (int i = 0; i < timeList.size(); ++i) {
							mDateDisplayValues[i] = timeList.get(i);
						}

						waitDialog.dismiss();
					} else {
						waitDialog.dismiss();
						Toast.makeText(OrderConfirmActivity.this,
								sendTimeBean.msg + "", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				Toast.makeText(OrderConfirmActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	/**
	 * 商品结算 获取服务器生成的订单
	 */
	public void getOrder() {
		StringBuffer sb = new StringBuffer();
		if (arrayList != null && arrayList.size() != 0) {
			for (int i = 0; i < arrayList.size(); i++) {
				FoodCarModel foodCarModel = arrayList.get(i);
				sb.append(foodCarModel.id + "_" + foodCarModel.daiSumNum + "_"
						+ foodCarModel.each_bag_money * foodCarModel.daiSumNum);
				if (i != arrayList.size() - 1) {
					sb.append(",");
				}
			}
			order_info = sb.toString();
		} else {
			Toast.makeText(OrderConfirmActivity.this, "请先选择商品哟 亲 ^_^",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (address == null || "".equals(address) || mobile == null
				|| "".equals(mobile) || contact == null | "".equals(contact)) {
			Toast.makeText(OrderConfirmActivity.this, "请先填写完整的地址哟 ^_^",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (reach_time == null || "".equals(reach_time)) {
			Toast.makeText(OrderConfirmActivity.this, "请选择派送时间哟 ^_^",
					Toast.LENGTH_SHORT).show();
			return;
		}

		// order_info -- 订单的信息[如:品种ID_购买数量_单个商品总价格(1_2_3,2_2_6)]
		// receiver -- 收货人姓名
		// mobile -- 收货人手机号
		// address -- 收货人地址
		// pay_status -- 付款方式(1代表在线支付,2代表货到付款)
		// reach_time -- 送达时间不能为空[如:2015-10-16_10:00-10:30]
		// beizhu -- 备注
		// use_coupon_id-- 使用券ID
		// money -- 实际支付总价格

		// if(contact!=null&&!"".equals(contact) &&
		// receive_address!=null&&!"".equals(receive_address) &&
		// detailed_address!=null&&!"".equals(detailed_address)){
		// if(!UserUtil.isMobileNO(mobile)){
		// Toast.makeText(AddAddressActivity.this, "电话号码格式错误",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		waitDialog.show();
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.getOrder;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("user_id", usrId);
		params.addBodyParameter("money", money + ""); // 总金额（减去了优惠券的 如果）
		if (!"0".equals(use_coupon_id)) {
			params.addBodyParameter("use_coupon_id", use_coupon_id + ""); // 优惠券
		}
		params.addBodyParameter("beizhu", beizhu + ""); // 备注
		params.addBodyParameter("reach_time", reach_time + ""); // 选择派送时间
		params.addBodyParameter("pay_status", pay_status + ""); // 支付方式 在线或者货到付款
		params.addBodyParameter("address", address + "");
		params.addBodyParameter("mobile", mobile + ""); // 电话
		params.addBodyParameter("receiver", contact + ""); // 姓名
		params.addBodyParameter("order_info", order_info + "");
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				waitDialog.dismiss();
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("OrderConfirmActivity", result + "");
					Gson gson = new Gson();
					msgBean = gson.fromJson(result, OrderBean.class);
					if ("1".equals(msgBean.code)) {
						// pay_status = 1; //1在线支付(默认) 2货到付款
						if (pay_status == 2) {
							Toast.makeText(OrderConfirmActivity.this,
									"货到付款订单已生成，请亲耐心敬候哟 ＾－＾", Toast.LENGTH_SHORT)
									.show();
							// 发送 清空购物车
							BfApplication.carMap.clear();
							EventBus.getDefault()
									.post(new EvenBusBean("clear"));
							EventBus.getDefault().post(
									new EvenBusBean("search_change"));
							EventBus.getDefault().post(
									new EvenBusBean("car_change"));

							Intent intent = new Intent(
									OrderConfirmActivity.this,
									OrderListActivity.class);
							startActivity(intent);
							OrderConfirmActivity.this.finish();
						} else if (pay_status == 1) {
							// Toast.makeText(OrderConfirmActivity.this,
							// "测试提示---订单已经生成可以选择支付方式了",
							// Toast.LENGTH_SHORT).show();
							// 打开支付方式选择框（微信或支付宝） 记得要清空购物车呀
							initpay(msgBean.data.ordernum);
							BfApplication.carMap.clear();
							EventBus.getDefault()
									.post(new EvenBusBean("clear"));
						}
					} else {
						waitDialog.dismiss();
						Toast.makeText(OrderConfirmActivity.this,
								msgBean.msg + "", Toast.LENGTH_SHORT).show();
					}

				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				Toast.makeText(OrderConfirmActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});

	}

	/*************** 回调操作 ***************************/
	// Eventbus回调
	public void onEventMainThread(EvenBusBean event) {

		String msg1 = event.getMsg();
		String msg2 = event.getMsg2();
		if ("beizhu".equals(msg1)) {
			tv_text.setText(msg2);
			beizhu = msg2;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 选址地址
		if (resultCode == RESULT_OK && requestCode == 0) {
			contact = data.getStringExtra("contact"); // 姓名
			address_id = data.getStringExtra("address_id");
			mobile = data.getStringExtra("mobile"); // 电话
			String receive_address = data.getStringExtra("receive_address"); // 地址
			String detailed_address = data.getStringExtra("detailed_address"); // 详细地址

			address = receive_address + "  " + detailed_address;

			tv_name.setText(contact + "  " + mobile);
			tv_name.setVisibility(View.VISIBLE);
			tv_addr.setText(receive_address + "  " + detailed_address);
		}

		// 选择优惠券
		if (resultCode == RESULT_OK && requestCode == 1) {
			use_coupon_id = data.getStringExtra("use_coupon_id"); // id
			couponMoney = data.getStringExtra("couponMoney"); // 金额
			// 当取消了使用优惠则恢复原价
			money = sourceMoney;
			if (!"0".equals(couponMoney)) {
				ll_coupon.setVisibility(View.VISIBLE);
				try {
					tv_coupon.setText("减 ￥"
							+ FoodCarModel.convertDouble(Double
									.parseDouble(couponMoney)) + "");
				} catch (Exception e) {
				}
			} else {
				// 当取消了使用优惠则恢复原价
				money = sourceMoney;
				ll_coupon.setVisibility(View.GONE);
				tv_coupon.setText("减 ￥0.00");
			}
			// 改变总金额
			initSumMoney();
		}

		// 填写备注
		// if(resultCode==RESULT_OK&&requestCode==2){
		// String text = data.getStringExtra("text"); //text
		// tv_text.setText(text);
		// beizhu = text;
		// }
	}

	/**
	 * 
	 * @author czz
	 * @createdate 2015年11月11日 下午5:44:57
	 * @Description: (微信支付接口方法)
	 * 
	 */
	private void doWxPayById(String product_id, String total_fee) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("正在加载");
		pd.show();
		RequestParams params = new RequestParams();
		params.addBodyParameter("orderid", product_id);
		Log.v("orderid", product_id);
		params.addBodyParameter("total_fee", total_fee);
		params.addBodyParameter("status", "1");
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, Contants.DOWXPAYBYID, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						pd.dismiss();
						JSONObject object;
						try {
							object = new JSONObject(responseInfo.result);
							Log.v("object", object + "");
							JSONObject opt = (JSONObject) object.opt("data");
							if (object.getString("code").equals("1")) {
								if (opt != null) {
									PayReq req = new PayReq();
									req.appId = opt.optString("appid");
									req.partnerId = opt.optString("partnerid");
									req.prepayId = opt.optString("prepayid");
									req.nonceStr = opt.optString("noncestr");
									req.timeStamp = opt.optString("timestamp");
									req.packageValue = opt.optString("package");
									req.sign = opt.optString("sign");
									req.extData = "app data"; // optional
									// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
									int wxSdkVersion = api.getWXAppSupportAPI();
									if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
										api.sendReq(req);
									} else {
										Toast.makeText(
												OrderConfirmActivity.this,
												"请安装微信客户端或登录微信账号",
												Toast.LENGTH_LONG).show();
									}
								}
							} else {
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						pd.dismiss();
						Toast.makeText(OrderConfirmActivity.this,
								"网络加载异常，请稍后再试", Toast.LENGTH_SHORT).show();
					}

				});
	}

}