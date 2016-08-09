package com.example.administrator.myapplication.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.BfApplication;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.model.FoodCarModel;
import com.example.administrator.myapplication.model.FoodSearchModel;
import com.example.administrator.myapplication.model.FoodSearchModel.FoodSearch;
import com.example.administrator.myapplication.model.LowestBean;
import com.example.administrator.myapplication.utils.UserUtil;
import com.example.administrator.myapplication.view.WaitDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * 商品搜索页面
 * 
 * @author fengzi
 * 
 */
public class SearchActivity extends BaseActivity implements OnClickListener {

	private ListView lv_cm;
	private View button1;
	private EditText tv_search;
	private View layout;
	private PopupWindow pw;
	private int width;
	private LayoutParams lp;
	private RelativeLayout titleBar;
	private String[] record;
	private View tv_right;
	private WaitDialog waitDialog;
	protected List<FoodSearch> data;
	private ScAdapter scAdapter;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ArrayList<FoodCarModel> arrayList;
	private View rl_car;
	private CarAdapter carAdapter;
	private TextView shoppingnum;
	private TextView tv_sum_money;
	private PopupWindow recordPw;
	private InputMethodManager imm;
	private int i;
	protected double minMoney = 0; // 最低消费金额
	private double CarsunMoney; // 总价
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		waitDialog = new WaitDialog(this);
		minMoney = getIntent().getDoubleExtra("minMoney", 0); // 获取最低消费金额
		initOptions();
		getNetMinMoney(); // 网络获取最低消费金额

		lv_cm = (ListView) findViewById(R.id.lv_cm);
		tv_search = (EditText) findViewById(R.id.tv_search);
		titleBar = (RelativeLayout) findViewById(R.id.titleBar);
		tv_right = (View) findViewById(R.id.tv_right);
		button1 = findViewById(R.id.button1);
		View ll_pay = findViewById(R.id.ll_pay);

		rl_car = findViewById(R.id.rl_car); // 购物车
		shoppingnum = (TextView) findViewById(R.id.shoppingnum); // 总数量
		tv_sum_money = (TextView) findViewById(R.id.tv_sum_money); // 总价
		scAdapter = new ScAdapter();
		// 进来先加载购物车
		compute();

		button1.setOnClickListener(this);
		tv_search.setOnClickListener(this);
		tv_right.setOnClickListener(this);
		rl_car.setOnClickListener(this);
		ll_pay.setOnClickListener(this);
		tv_search.setOnKeyListener(new jpOnKeyListener());

	}

	public void initOptions() {
		options = new DisplayImageOptions.Builder()
				// .showImageForEmptyUri(R.drawable.ic_empty)
				// .showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		imageLoader = ImageLoader.getInstance();
	}

	// 修改键盘 回车键为搜索功能
	class jpOnKeyListener implements OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER) {// 修改回车键功能
				// 先隐藏键盘
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(SearchActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				searchStart();
			}

			return false;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			finish();
			break;
		case R.id.tv_search:

			if (i == 0) {
				record();
				i = 1;
			}
			break;
		case R.id.tv_right:
			// 先隐藏键盘
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(SearchActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			String text = tv_search.getText().toString();
			if (text != null && !"".equals(text)) {
				// 搜索按钮
				initNetData(text);
				saveRecord(text);
			} else {
				showShortToastMessage("搜索内容不能为空");
			}
			break;
		case R.id.rl_car: // 购物车
			showCar();
			break;
		case R.id.ll_pay: // 结算 当大于最低消费金额时才可以结算
			if (!islogin()) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				return;
			}
			if (CarsunMoney >= minMoney) {
				Intent intentpay = new Intent(SearchActivity.this,
						OrderConfirmActivity.class);
				startActivity(intentpay);
			} else {
				Toast.makeText(this, "亲，购满" + minMoney + "才可以结算哟",
						Toast.LENGTH_SHORT).show();
			}
		default:
			break;
		}
	}

	// 蔬菜 内容适配器
	class ScAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View order_itme = getLayoutInflater().inflate(
					R.layout.food_content_itme, null);

			ImageView iv_jian = (ImageView) order_itme
					.findViewById(R.id.iv_jian);
			ImageView iv_jia = (ImageView) order_itme.findViewById(R.id.iv_jia);
			EditText tv_num = (EditText) order_itme.findViewById(R.id.tv_num);
			TextView tv_dai_num = (TextView) order_itme
					.findViewById(R.id.tv_dai_num); // 代数
			// TextView tv_jing_num = (TextView)
			// order_itme.findViewById(R.id.tv_jing_num); //总斤数
			TextView tvdanwei = (TextView) order_itme
					.findViewById(R.id.tvdanwei);

			TextView tv_title = (TextView) order_itme
					.findViewById(R.id.tv_title);
			TextView tv_content = (TextView) order_itme
					.findViewById(R.id.tv_content);
			TextView tv_each_bag_money = (TextView) order_itme
					.findViewById(R.id.tv_each_bag_money); // 每袋价格
			TextView tv_pounds = (TextView) order_itme
					.findViewById(R.id.tv_pounds); // 每袋多少斤
			TextView tv_pounds_money = (TextView) order_itme
					.findViewById(R.id.tv_pounds_money); // 每斤的单价
			ImageView iv_pic_name = (ImageView) order_itme
					.findViewById(R.id.iv_pic_name); // 图片
			FoodSearch foodContent = data.get(position);
			tv_title.setText(foodContent.title);
			tv_content.setText(foodContent.content);
			tv_each_bag_money.setText(foodContent.each_bag_money + "元"); // 每袋价格
			tvdanwei.setText("/" + foodContent.each_bag_unit);
			tv_pounds.setText(foodContent.poundsunit); // 每袋多少斤
			tv_pounds_money.setText("每" + foodContent.each_bag_unit
					+ foodContent.each_bag_money + "元"); // 每斤的单价

			imageLoader.displayImage(
					Contants.GLOBAL_URL + foodContent.pic_name, iv_pic_name,
					options);
			// 显示收买的宝贝数量
			showNum(position, tv_num, tv_dai_num, iv_jian);

			// 添加删除宝贝
			iv_jian.setOnClickListener(new CarJiaJianOnClickListener(position,
					tv_num, tv_dai_num, iv_jian)); // 添加
			iv_jia.setOnClickListener(new CarJiaJianOnClickListener(position,
					tv_num, tv_dai_num, iv_jian)); // 减少
			return order_itme;
		}

	}

	/************* 列表上的宝贝保存数据量操作 **********************/
	// 显示已经拍的宝贝数据量
	private void showNum(int position, TextView tv_num, TextView tv_dai_num,
			View iv_jian) {
		// 获取商品id先和本地购物车模型中的对比 看是否加入过
		FoodSearch foodSearch = data.get(position);
		String id = foodSearch.id;

		Map<String, FoodCarModel> carMap = BfApplication.carMap;
		FoodCarModel foodCarModel = carMap.get(id);
		// 已经加入 显示出来
		if (foodCarModel != null) {
			iv_jian.setVisibility(View.VISIBLE);
			tv_num.setVisibility(View.VISIBLE);
			tv_num.setText(foodCarModel.daiSumNum + "");
			tv_dai_num.setText(foodCarModel.daiSumNum + "");
			// tv_jing_num.setText(foodCarModel.pounds*foodCarModel.daiSumNum+"");
		} else { // 未加入 就不显示了

		}

	}

	// 添加删除宝贝
	class CarJiaJianOnClickListener implements OnClickListener {
		int position;
		TextView tv_num;
		TextView tv_dai_num;
		// TextView tv_jing_num;
		View iv_jian;

		public CarJiaJianOnClickListener(int position, TextView tv_num,
				TextView tv_dai_num, View iv_jian) {
			this.position = position;
			this.tv_num = tv_num;
			this.tv_dai_num = tv_dai_num;
			this.iv_jian = iv_jian;
		}

		@Override
		public void onClick(View v) {
			// 获取商品id先和本地购物车模型中的对比 看是否加入过
			FoodSearch foodSearch = data.get(position);
			String id = foodSearch.id;

			Map<String, FoodCarModel> carMap = BfApplication.carMap;
			FoodCarModel foodCarModel = carMap.get(id);
			// 已经加入
			if (foodCarModel != null) {
				switch (v.getId()) {
				case R.id.iv_jia: // 增加
					foodCarModel.daiSumNum = foodCarModel.daiSumNum + 1;
					tv_num.setText(foodCarModel.daiSumNum + "");
					tv_dai_num.setText(foodCarModel.daiSumNum + "");
					// tv_jing_num.setText(foodCarModel.pounds*foodCarModel.daiSumNum+"");
					carMap.put(id, foodCarModel);
					// 改变购物车
					compute();

					break;
				case R.id.iv_jian: // 减少
					if (foodCarModel.daiSumNum - 1 > 0) {
						foodCarModel.daiSumNum = foodCarModel.daiSumNum - 1;
						tv_num.setText(foodCarModel.daiSumNum + "");
						tv_dai_num.setText(foodCarModel.daiSumNum + "");
						// tv_jing_num.setText(foodCarModel.pounds*foodCarModel.daiSumNum+"");
						carMap.put(id, foodCarModel);
					} else if (foodCarModel.daiSumNum - 1 == 0) { // 到0时不减少
																	// 并隐藏减少按钮

						iv_jian.setVisibility(View.GONE);
						tv_num.setVisibility(View.GONE);
						foodCarModel.daiSumNum = foodCarModel.daiSumNum - 1;
						tv_num.setText("0");
						tv_dai_num.setText("0");
						// tv_jing_num.setText("0");
						carMap.remove(id);
					}

					// 改变购物车
					compute();

					break;

				default:
					break;
				}
			} else { // 未加入 只有按加按钮 才会走这里 添加第一商品
				FoodCarModel newfoodCarModel = new FoodCarModel();
				newfoodCarModel.id = foodSearch.id;
				newfoodCarModel.title = foodSearch.title;
				newfoodCarModel.each_bag_money = Double
						.parseDouble(foodSearch.each_bag_money); // 每袋单价
				newfoodCarModel.daiSumNum = 1; // 总代数
				// newfoodCarModel.pounds =
				// Double.parseDouble(foodSearch.pounds); //每袋斤数

				carMap.put(id, newfoodCarModel);

				iv_jian.setVisibility(View.VISIBLE);
				tv_num.setVisibility(View.VISIBLE);
				tv_num.setText("1");
				tv_dai_num.setText("1");
				// tv_jing_num.setText(newfoodCarModel.pounds*newfoodCarModel.daiSumNum+"");
				// 改变购物车
				compute();

			}

		}

	}

	/************ 购物车中的宝贝保存数据量操作 **********************/

	/**
	 * 显示购物车 **************购物车模块*******************
	 */
	public void showCar() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.dialog_layout_car, null);
		setView(layout);
		pw = new PopupWindow(layout);

		// pw.setAnimationStyle(R.style.searchDialogAnimation); //设置动画

		pw.setFocusable(true);// 设置可以获得焦点
		pw.setTouchable(true); // 设置弹窗内可点击
		pw.setOutsideTouchable(false);// 设置非PopupWindow区域可触摸

		pw.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				pw.dismiss();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});

		// 获取到屏幕尺寸 设置对话框大小
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels;
		height = (int) (height * 0.43);

		ListView lv_car = (ListView) layout.findViewById(R.id.lv_car);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lv_car
				.getLayoutParams();
		height = layoutParams.height;
		pw.setHeight(height);
		pw.setWidth(width);

		// 清空购物车
		TextView tv_del_all = (TextView) layout.findViewById(R.id.tv_del_all);
		tv_del_all.setOnClickListener(new CarAllOnClickListener());
		// 产生背景变暗效果 背景一定要在这里设置
		ColorDrawable cd = new ColorDrawable(0x000000);
		pw.setBackgroundDrawable(cd);
		lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		pw.showAsDropDown(rl_car, 0, 20);
	}

	public void setView(View layout) {
		arrayList = new ArrayList<FoodCarModel>();
		Map<String, FoodCarModel> carMap = BfApplication.carMap;
		Set<String> keySetCar = carMap.keySet();
		if (keySetCar == null) {
			return;
		}

		for (String id : keySetCar) {
			arrayList.add(carMap.get(id));
		}

		ListView lv_car = (ListView) layout.findViewById(R.id.lv_car);
		carAdapter = new CarAdapter();
		if (arrayList != null) {
			lv_car.setAdapter(carAdapter);
		}

	}

	class CarAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
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
			View view = getLayoutInflater().inflate(
					R.layout.dialog_layout_car_itme, null);
			TextView tv_sun_price = (TextView) view
					.findViewById(R.id.tv_sun_price); // 每袋价格
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			TextView tv_dai_num = (TextView) view.findViewById(R.id.tv_dai_num); // 数量（总代数）
			ImageView iv_jian = (ImageView) view.findViewById(R.id.iv_jian);
			ImageView iv_jia = (ImageView) view.findViewById(R.id.iv_jia);

			FoodCarModel foodCarModel = arrayList.get(position);
			tv_title.setText(foodCarModel.title + "");
			tv_dai_num.setText(foodCarModel.daiSumNum + "");

			tv_sun_price.setText("￥"
					+ FoodCarModel.convertDouble(foodCarModel.daiSumNum
							* foodCarModel.each_bag_money) + "元");

			// 购物车加减监听
			iv_jian.setOnClickListener(new CarOnClickListener(position,
					tv_dai_num, tv_sun_price, foodCarModel));
			iv_jia.setOnClickListener(new CarOnClickListener(position,
					tv_dai_num, tv_sun_price, foodCarModel));
			return view;
		}

	}

	// 购物车中添加删除宝贝
	class CarOnClickListener implements OnClickListener {
		int position;
		TextView tv_dai_num;
		TextView tv_sun_price;
		FoodCarModel foodCarModel_list;

		public CarOnClickListener(int position, TextView tv_dai_num,
				TextView tv_sun_price, FoodCarModel foodCarModel) {
			this.position = position;
			this.tv_dai_num = tv_dai_num;
			this.tv_sun_price = tv_sun_price;
			this.foodCarModel_list = foodCarModel;
		}

		@Override
		public void onClick(View v) {
			String id = foodCarModel_list.id;

			Map<String, FoodCarModel> carMap = BfApplication.carMap;
			// 根据id获取到过车模型中的对象
			FoodCarModel foodCarModel = carMap.get(id);
			// 已经加入
			switch (v.getId()) {
			case R.id.iv_jia: // 增加
				foodCarModel.daiSumNum = foodCarModel.daiSumNum + 1;
				tv_dai_num.setText(foodCarModel.daiSumNum + "");
				tv_sun_price.setText("￥"
						+ FoodCarModel
								.convertDouble(foodCarModel.each_bag_money
										* foodCarModel.daiSumNum) + "元");
				carMap.put(id, foodCarModel);
				// 改变总价
				compute();
				// 发送
				EventBus.getDefault().post(new EvenBusBean("car_change"));
				break;
			case R.id.iv_jian: // 减少
				if (foodCarModel.daiSumNum - 1 > 0) {
					foodCarModel.daiSumNum = foodCarModel.daiSumNum - 1;
					tv_dai_num.setText(foodCarModel.daiSumNum + "");
					tv_sun_price.setText("￥"
							+ FoodCarModel
									.convertDouble(foodCarModel.each_bag_money
											* foodCarModel.daiSumNum) + "元");
					carMap.put(id, foodCarModel);
				} else if (foodCarModel.daiSumNum - 1 == 0) { // 到1时 减1 删除该条目

					foodCarModel.daiSumNum = foodCarModel.daiSumNum - 1;
					tv_dai_num.setText("0");
					tv_sun_price.setText("￥0元");
					carMap.remove(id);

					// 判断是不是最后一个宝贝的时候
					if (arrayList.size() == 1) {
						// 改变总价
						compute();
						pw.dismiss();
						return;
					}
					// 不是最后一个宝贝 删除那个宝贝然后再改变适配器 重新显示剩下的宝贝
					arrayList.clear();
					arrayList = new ArrayList<FoodCarModel>();
					Map<String, FoodCarModel> carMaps = BfApplication.carMap;
					Set<String> keySetCar = carMaps.keySet();
					for (String ids : keySetCar) {
						arrayList.add(carMap.get(ids));
					}
					carAdapter.notifyDataSetChanged();

				}
				// 改变总价
				compute();
				break;

			default:
				break;
			}

		}

	}

	// 计算总价 加载购物车
	public void compute() {
		// 发送 让上个页面的数据也联动发生数据改变（改变上个页面的购物车和列表记录）
		EventBus.getDefault().post(new EvenBusBean("car_change"));
		EventBus.getDefault().post(new EvenBusBean("search_change"));
		// 本页列表数据量重新适配
		scAdapter.notifyDataSetChanged();

		int sumNum = 0;
		double sunMoney = 0;
		Map<String, FoodCarModel> carMap = BfApplication.carMap;
		Set<String> keySet = carMap.keySet();
		for (String id : keySet) {
			FoodCarModel foodCarModel = carMap.get(id);
			sumNum = sumNum + foodCarModel.daiSumNum;
			sunMoney = sunMoney + foodCarModel.each_bag_money
					* foodCarModel.daiSumNum;
		}
		if (sumNum != 0) {
			shoppingnum.setVisibility(View.VISIBLE);
		} else {
			shoppingnum.setVisibility(View.GONE);
		}
		shoppingnum.setText(sumNum + "");
		tv_sum_money.setText("￥" + FoodCarModel.convertDouble(sunMoney) + "");
		CarsunMoney = sunMoney;
	}

	/************************************** 历史记录 ********************/
	/**
	 * 打开历史对话框
	 */
	@SuppressLint("NewApi")
	private void record() {

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(tv_search.getWindowToken(), 0); // 强制隐藏键盘

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.dialog_layout_record, null);
		recordPw = new PopupWindow(layout);
		show(layout); // 设置布局上的数据

		// recordPw.setAnimationStyle(R.style.searchDialogAnimation); //设置动画

		recordPw.setFocusable(true);// 设置可以获得焦点
		recordPw.setTouchable(true); // 设置弹窗内可点击
		recordPw.setOutsideTouchable(false);// 设置非PopupWindow区域可触摸

		recordPw.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				recordPw.dismiss();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
				imm.showSoftInput(tv_search, InputMethodManager.SHOW_FORCED); // 强制显示键盘
			}
		});

		// 获取到屏幕尺寸 设置对话框大小
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels;
		height = (int) (height * 0.43);

		LinearLayout ll_kk = (LinearLayout) layout.findViewById(R.id.ll_kk);
		ViewGroup.LayoutParams layoutParams = ll_kk
				.getLayoutParams();
		height = layoutParams.height;
		recordPw.setHeight(height);
		recordPw.setWidth(width);

		// 产生背景变暗效果 背景一定要在这里设置
		ColorDrawable cd = new ColorDrawable(0x000000);
		recordPw.setBackgroundDrawable(cd);
		lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		recordPw.showAsDropDown(titleBar, 0, Gravity.BOTTOM);

	}

	/**
	 * 显示历史对话框数据
	 * 
	 * @param v
	 */
	public void show(View v) {
		int length = 0;
		TextView tv_clean = (TextView) v.findViewById(R.id.tv_clean);
		TextView tv1 = (TextView) v.findViewById(R.id.tv1);
		TextView tv2 = (TextView) v.findViewById(R.id.tv2);
		TextView tv3 = (TextView) v.findViewById(R.id.tv3);
		View ve1 = v.findViewById(R.id.ve1);
		View ve2 = v.findViewById(R.id.ve2);
		View ve3 = v.findViewById(R.id.ve3);
		record = getRecord();
		if (record != null) {
			length = record.length;
			if (length > 3) {
				length = 3;
			}
			switch (length) {
			case 1:
				tv1.setVisibility(View.VISIBLE);
				ve1.setVisibility(View.VISIBLE);
				tv1.setText(record[0]);
				break;
			case 2:
				tv1.setVisibility(View.VISIBLE);
				ve1.setVisibility(View.VISIBLE);
				tv2.setVisibility(View.VISIBLE);
				ve2.setVisibility(View.VISIBLE);
				tv1.setText(record[0]);
				tv2.setText(record[1]);
				break;
			case 3:
				tv1.setVisibility(View.VISIBLE);
				ve1.setVisibility(View.VISIBLE);
				tv2.setVisibility(View.VISIBLE);
				ve2.setVisibility(View.VISIBLE);
				tv3.setVisibility(View.VISIBLE);
				ve3.setVisibility(View.VISIBLE);
				tv1.setText(record[0]);
				tv2.setText(record[1]);
				tv3.setText(record[2]);
				break;

			default:
				break;
			}
		} else {
			tv_clean.setText("没有历史记录");
		}

		tv1.setOnClickListener(new RecOnClickListener());
		tv2.setOnClickListener(new RecOnClickListener());
		tv3.setOnClickListener(new RecOnClickListener());
		if (length > 0) {
			tv_clean.setOnClickListener(new RecOnClickListener());
		}

	}

	// 显示数据到搜索栏上
	class RecOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.tv1:
				tv_search.setText(record[0]);
				recordPw.dismiss();
				searchStart();
				break;
			case R.id.tv2:
				tv_search.setText(record[1]);
				recordPw.dismiss();
				searchStart();
				break;
			case R.id.tv3:
				tv_search.setText(record[2]);
				recordPw.dismiss();
				searchStart();
				break;
			case R.id.tv_clean:
				cleanRecord();
				recordPw.dismiss();
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 调用网络请求 开始搜索产品
	 */
	public void searchStart() {
		String text = tv_search.getText().toString();
		if (text != null && !"".equals(text)) {
			// 搜索按钮
			initNetData(text);
			saveRecord(text);
		} else {
			showShortToastMessage("搜索内容不能为空");
		}
	}

	/**
	 * dip转换xp
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 获取历史搜索
	 * 
	 * @return
	 */
	public String[] getRecord() {
		SharedPreferences spRecord = getSharedPreferences("spRecord", 0);
		String record = spRecord.getString("record", "");
		if (!"".equals(record)) {
			String[] split = record.split("_");
			return split;
		}
		return null;
	}

	/**
	 * 保存历史搜索
	 * 
	 * @param str
	 */
	public void saveRecord(String str) {
		SharedPreferences spRecord = getSharedPreferences("spRecord", 0);
		String record = spRecord.getString("record", "");

		String[] split = record.split("_");
		if (split.length > 3) {
			if ((split[0] + split[1] + split[2]).contains(str)) {
				return;
			}
		}
		if (split.length > 2) {
			if ((split[0] + split[1]).contains(str)) {
				return;
			}
		}
		if (split.length > 1) {
			if (split[0].contains(str)) {
				return;
			}
		}

		record = str + "_" + record;
		if (record.length() > 30) {
			String substring = record.substring(0, 29);
		}

		Editor edit = spRecord.edit();
		edit.putString("record", record).commit();

	}

	/**
	 * 清除历史
	 */

	public void cleanRecord() {
		SharedPreferences spRecord = getSharedPreferences("spRecord", 0);
		Editor edit = spRecord.edit();
		edit.clear().commit();
	}

	/*********************** 网络请求 ***************************/

	public void initNetData(String text) {
		// 拿到城市
		String province = MainActivity.province;
		String city = MainActivity.city;
		if ("请选择".equals(province) || "请选择".equals(city)) {
			Toast.makeText(SearchActivity.this, "亲，请先选择城市哟！",
					Toast.LENGTH_SHORT).show();
			return;
		}

		waitDialog.show();
		HttpUtils http = new HttpUtils();
		String url = Contants.searchGoods;
		RequestParams params = new RequestParams();

		params.addBodyParameter("province", province);
		params.addBodyParameter("city", city); // 必传这2个参数
		params.addBodyParameter("title", text); // 标题
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {

					String result = responseInfo.result;
					Log.e("serarchactivity", result + "");
					Gson gson = new Gson();
					FoodSearchModel foodSearchModel = gson.fromJson(result,
							FoodSearchModel.class);
					if ("1".equals(foodSearchModel.code)) {
						data = foodSearchModel.data;
						if (data != null) {
							lv_cm.setAdapter(scAdapter);
							lv_cm.setVisibility(View.VISIBLE);

						} else {
							lv_cm.setVisibility(View.INVISIBLE);
							Toast.makeText(SearchActivity.this,
									"亲，没有搜索到您需要的产品哟", Toast.LENGTH_SHORT)
									.show();
						}

					} else {
						lv_cm.setVisibility(View.INVISIBLE);
						Toast.makeText(SearchActivity.this, "亲，没有搜索到您需要的产品哟",
								Toast.LENGTH_SHORT).show();
					}
				}

				waitDialog.dismiss();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(SearchActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
				waitDialog.dismiss();
			}

		});
	}

	/**
	 * 获取最低消费金额
	 */
	public void getNetMinMoney() {
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.lowest;
		// 添加请求参数
		http.send(HttpMethod.POST, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("MainActivity", result + "");
					Gson gson = new Gson();
					LowestBean lowestBean = gson.fromJson(result,
							LowestBean.class);
					if ("1".equals(lowestBean.code)) {
						String minMoneyStr = lowestBean.data.money;
						if (minMoneyStr != null && !"".equals(minMoneyStr)) {
							minMoney = Double.parseDouble(minMoneyStr);
						}

						// 收货完成操作
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(SearchActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	/*
	 * 判断是否在线
	 */
	public boolean islogin() {
		String usrId = UserUtil.getUsrId(this);
		if (!"no".equals(usrId)) {
			return true;
		}
		return false;

	}

	// 点击清空购车

	class CarAllOnClickListener implements OnClickListener {

		public CarAllOnClickListener() {
		}

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					SearchActivity.this);
			builder.setMessage("确认清空购物车？");
			builder.setPositiveButton("清空",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							pw.dismiss();
							// 清空购车
							BfApplication.carMap.clear();
							// 改变总价
							compute();
							tv_sum_money.setText("￥ 0.00");
							// 发送
							EventBus.getDefault().post(
									new EvenBusBean("car_change"));
						}
					});
			builder.setNegativeButton("取消", null);
			builder.show();

		}

	}

}
