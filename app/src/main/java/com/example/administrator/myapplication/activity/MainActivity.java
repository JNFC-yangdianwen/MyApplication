package com.example.administrator.myapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.buyfood.R;
import com.example.administrator.myapplication.adapter.MyPagerAdapter;
import com.example.administrator.myapplication.fragment.ItemFragment;
import com.example.administrator.myapplication.fragment.ItemFragment.OnArticleSelectedListener;
import com.example.administrator.myapplication.globle.BfApplication;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.model.FoodCarModel;
import com.example.administrator.myapplication.model.FoodModel;
import com.example.administrator.myapplication.model.FoodModel.FoodData.Menu1;
import com.example.administrator.myapplication.model.FoodModel.FoodData.Notice;
import com.example.administrator.myapplication.model.GetVerResponse;
import com.example.administrator.myapplication.model.LowestBean;
import com.example.administrator.myapplication.model.UserInfoBean;
import com.example.administrator.myapplication.model.UserInfoBean.UserInfo;
import com.example.administrator.myapplication.utils.AppUtil;
import com.example.administrator.myapplication.utils.CommonUtil;
import com.example.administrator.myapplication.utils.PxAndDip;
import com.example.administrator.myapplication.utils.UserUtil;
import com.example.administrator.myapplication.view.AutoScrollViewPager;
import com.example.administrator.myapplication.view.AutoScrollViewPager.OnPageClickListener;
import com.example.administrator.myapplication.view.NoViewPager;
import com.example.administrator.myapplication.viewpagerindicator.TabPageIndicator;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * 首页
 *
 * @author zm04
 *
 */
public class MainActivity extends SlidingFragmentActivity implements
		OnClickListener, OnArticleSelectedListener {

	public static MainActivity mainActivity;

	/**
	 * Tab标题
	 */
	List<String> TITLE = new ArrayList<String>();

	private SlidingMenu slidingMenu;
	private View ll_title1;
	private View ll_title6;
	private View ll_userinfo;
	private View ll_title5;
	private View ll_title4;
	private View ll_title2;
	private View ll_title3;

	protected List<Notice> notice_list; // 通知集合
	protected List<Menu1> parent_list; // food集合

	protected int selectMenu1; // 当前的选择菜单项 从0开始
	private TextView shoppingnum;

	private TextView tv_sum_money;

	private ImageView iv_car;

	private int width;

	private View layout;

	private PopupWindow pw;

	private LayoutParams lp;

	private ArrayList<FoodCarModel> arrayList;

	private CarAdapter carAdapter;

	private View rl_car;

	protected double minMoney = 0; // 最低消费金额

	private double CarsunMoney; // 总价

	private TextView tv_nickname;

	private TextView tv_mobile;

	private String usrId;

	private View ll_login;

	private View ll_logined;

	private TextView title_but_right;

	private TabPageIndicatorAdapter adapter;

	// 省和城市
	public static String province;
	public static String city;

	private View ll_content;

	private long exitTime;

	private TextView tvXd;

	private View ll_pay;

	private AutoScrollViewPager asvp;

	private View llTongZhi;

	private ListView lv_car;

	private View tvNodata;

	public static String dist;

	private LocationClient mLocationClient;

	private SharedPreferences adrrsp;

	private ProgressDialog pd;

	private Fragment fragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initSliding();
		mainActivity = this;
		// 注册eventbus
		EventBus.getDefault().register(this);

		title_but_right = (TextView) findViewById(R.id.title_but_right); // 城市选择
		ll_content = findViewById(R.id.ll_content); // 城市选择
		// 设置地址
		adrrsp = getApplicationContext().getSharedPreferences("useradrr", 0);
		province = adrrsp.getString("province", "请选择");
		city = adrrsp.getString("city", "请选择");
		dist = adrrsp.getString("dist", "请选择");
		title_but_right.setText(city);
		if (province.contains("北京") || province.contains("天津")
				|| province.contains("上海") || province.contains("重庆")) {
			title_but_right.setText(city);
		} else {
			title_but_right.setText(dist);
		}
		View button1 = findViewById(R.id.button1);
		ll_pay = findViewById(R.id.ll_pay);
		View tv_search = findViewById(R.id.tv_search); // 搜索

		asvp = (AutoScrollViewPager) findViewById(R.id.asvp); // 轮播广告
		shoppingnum = (TextView) findViewById(R.id.shoppingnum); // 总数量
		tv_sum_money = (TextView) findViewById(R.id.tv_sum_money); // 总价
		iv_car = (ImageView) findViewById(R.id.iv_car); // 购物车
		rl_car = findViewById(R.id.ll_carnum); // 点击购物车
		llTongZhi = findViewById(R.id.llTongZhi); // 通知栏
		tvXd = (TextView) findViewById(R.id.tvXd);
		tvNodata = findViewById(R.id.tvNodata);
		// 数据初始化
		getNetMinMoney(); // 获取最低消费金额
		pd = new ProgressDialog(this);
		pd.setMessage("正在定位");
		pd.show();
		initbaidu();

		getNetUserInfo(); // 初始化侧拉栏的用户名
		geVerInfo(); // 检查更新
		button1.setOnClickListener(this);
		ll_pay.setOnClickListener(this);
		tv_search.setOnClickListener(this);
		rl_car.setOnClickListener(this);
		title_but_right.setOnClickListener(this);
	}

	// 百度定位
	public void initbaidu() {
		mLocationClient = new LocationClient(this); // 声明LocationClient类
		// 设置定位条件
		LocationClientOption option = new LocationClientOption();
		// option.setOpenGps(true); //是否打开GPS
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(3000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new MyLocationListener()); // 注册监听函数
		mLocationClient.start();
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			pd.dismiss();
			if (location == null) {
				Toast.makeText(mainActivity, "定位失败", Toast.LENGTH_SHORT).show();
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			// Log.e("定位：", location.getAddrStr()+"");
			// Log.e("定位：", sb.toString()+"");
			// Log.e("定位：", location.getProvince() +"省份");
			// Log.e("定位：", location.getLatitude() +"纬度");
			// Log.e("定位：", location.getLongitude() +"经度");
			Log.i("定位：", location.getDistrict() + "区县");
			province = location.getProvince();
			city = location.getCity();
			dist = location.getDistrict();
			BfApplication.latitude = location.getLatitude();
			BfApplication.longitude = location.getLongitude();
			BfApplication.addr = location.getAddrStr();
			String district = location.getDistrict();
			if (province != null) {
				if (province.contains("市") || province.contains("省")) {
					province = province.substring(0, province.length() - 1);
				}
				Log.i("定位：", province + "处理后的省份---------" + city);
				Editor edit = adrrsp.edit();
				edit.putString("province", province).commit();
				edit.putString("latitude", BfApplication.latitude + "")
						.commit();
				edit.putString("Longitude", BfApplication.longitude + "")
						.commit();
				// 直辖市城市直接用区
				if (province.contains("北京") || province.contains("天津")
						|| province.contains("上海") || province.contains("重庆")) {
					edit.putString("city", district).commit();
					title_but_right.setText(district);
				} else {
					edit.putString("city", city).commit();
					edit.putString("dist", dist).commit();
					title_but_right.setText(dist);
				}
				mLocationClient.stop();
				initData();

			}

		}

	}

	public void initfrmg() {

		// ViewPager的adapter
		adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
		NoViewPager pager = (NoViewPager) findViewById(R.id.pager);
		// 设置缓存页面，当前页面的相邻N各页面都会被缓存
		pager.setOffscreenPageLimit(TITLE.size());
		// pager.setOffscreenPageLimit(0);
		if (TITLE.size() > 0) {
			pager.setCurrentItem(0);
		}
		pager.setAdapter(adapter);

		// 实例化TabPageIndicator然后设置ViewPager与之关联
//		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
//		indicator.setViewPager(pager);
//		indicator.setVisibility(View.VISIBLE);

		// 如果我们要对ViewPager设置监听，用indicator设置就行了
//			indicator.setOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int arg0) {
//				// 记录当前选择
//				selectMenu1 = arg0;
//
//				if (arg0 != 0) {
//					slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE); // 不响应触摸事件
//				} else {
//					slidingMenu
//							.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN); //
//				}
//				// Toast.makeText(getApplicationContext(), TITLE.get(arg0)+"",
//				// Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//
//			}
//		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.button1: // 侧拉栏
			toggle();
			break;
		case R.id.title_but_right: // 城市选择
			Intent intentcity = new Intent(this, CitiesActivity.class);
			startActivity(intentcity);
			break;

		case R.id.ll_pay: // 结算 当大于最低消费金额时才可以结算
			if (!islogin()) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				return;
			}
			if (CarsunMoney >= minMoney) {
				Intent intentpay = new Intent(MainActivity.this,
						OrderConfirmActivity.class);
				startActivity(intentpay);
			} else {
				Toast.makeText(this, "亲，购满" + minMoney + "才可以结算哟",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.ll_carnum: // 购物车
			showCar();
			break;
		case R.id.tv_search: // 搜索
			// 需要做判断
			Intent intentsearch = new Intent(MainActivity.this,
					SearchActivity.class);
			intentsearch.putExtra("minMoney", minMoney);
			startActivity(intentsearch);
			break;
		case R.id.ll_userinfo:// 个人信息
			if (!islogin()) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				toggle();
				return;
			}
			Intent intentinfo = new Intent(MainActivity.this,
					UserInfoActivity.class);
			startActivity(intentinfo);
			toggle();
			break;
		case R.id.ll_title1: // 我的订单
			if (!islogin()) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				toggle();
				return;
			}
			Intent intent1 = new Intent(MainActivity.this,
					OrderListActivity.class);
			startActivity(intent1);
			toggle();
			break;
		case R.id.ll_title2:// 收货地址
			if (!islogin()) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				toggle();
				return;
			}
			Intent intent2 = new Intent(MainActivity.this,
					SelectAddressActivity.class);
			startActivity(intent2);
			toggle();
			break;
		case R.id.ll_title3: // 我的优惠券
			if (!islogin()) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				toggle();
				return;
			}
			Intent intent = new Intent(this, UserCouponActivity.class);
			startActivity(intent);
			toggle();
			break;
		case R.id.ll_title4: // 邀请活动
			if (!islogin()) {
				Intent intentl = new Intent(this, LoginActivity.class);
				startActivity(intentl);
				toggle();
				return;
			}
			Intent intent4 = new Intent(MainActivity.this,
					UserInviteCodeActivity.class);
			startActivity(intent4);
			toggle();
			break;
		case R.id.ll_title5:
			AlertDialog.Builder bulider = new AlertDialog.Builder(this);
			bulider.setMessage("拨打" + getString(R.string.phone) + "?");
			bulider.setPositiveButton("拨打",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent intent5 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getString(R.string.phone)));
							startActivity(intent5);
						}
					});
			bulider.setNegativeButton("取消", null);
			bulider.show();
			toggle();
			break;
		case R.id.ll_title6:
			Intent intent6 = new Intent(MainActivity.this, SetUpActivity.class);
			startActivity(intent6);
			toggle();

			break;

		default:
			break;
		}
	}

	// 设置侧拉栏
	private void initSliding() {
		// 设置侧拉条目布局
		setBehindContentView(R.layout.menu_list);
		// 获取侧拉栏目对象
		slidingMenu = getSlidingMenu();
		ll_userinfo = slidingMenu.findViewById(R.id.ll_userinfo);
		ll_userinfo.setOnClickListener(this);
		ll_title1 = slidingMenu.findViewById(R.id.ll_title1);
		ll_title1.setOnClickListener(this);
		ll_title2 = slidingMenu.findViewById(R.id.ll_title2);
		ll_title2.setOnClickListener(this);
		ll_title3 = slidingMenu.findViewById(R.id.ll_title3);
		ll_title3.setOnClickListener(this);
		ll_title4 = slidingMenu.findViewById(R.id.ll_title4);
		ll_title4.setOnClickListener(this);
		ll_title5 = slidingMenu.findViewById(R.id.ll_title5);
		ll_title5.setOnClickListener(this);
		ll_title6 = slidingMenu.findViewById(R.id.ll_title6);
		ll_title6.setOnClickListener(this);

		tv_nickname = (TextView) slidingMenu.findViewById(R.id.tv_nickname);
		tv_mobile = (TextView) slidingMenu.findViewById(R.id.tv_mobile);
		ll_login = slidingMenu.findViewById(R.id.ll_login); // 去登录
		ll_logined = slidingMenu.findViewById(R.id.ll_logined); // 已登录
		// 设置用户名
		setname();

		/*
		 * SlidingMenu.TOUCHMODE_FULLSCREEN全屏触摸有效 SlidingMenu.TOUCHMODE_MARGIN
		 * 拖拽边缘有效 SlidingMenu.TOUCHMODE_NONE 不响应触摸事件
		 */
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// 设置内容显示页对应的dp大小
		// slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// //设置左侧侧拉栏目宽度
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int wwidth = wm.getDefaultDisplay().getWidth(); // 手机屏幕的宽度
		slidingMenu.setBehindWidth((int) (wwidth * 0.7));
		// 设置侧拉栏目所在位置
		/*
		 * SlidingMenu.LEFT SlidingMenu.LEFT_RIGHT SlidingMenu.RIGHT
		 */
		slidingMenu.setMode(SlidingMenu.LEFT);
		// 给侧拉栏目和左侧内容页区分开(加线)
		// slidingMenu.setShadowDrawable(R.drawable.shadow);
		// 设置线的宽度
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
	}

	// 设置用户名的方法
	public void setname() {
		SharedPreferences userconfig = MainActivity.this.getSharedPreferences(
				"userconfig", 0);
		String nickname = userconfig.getString("nickname", "");
		String mobile = userconfig.getString("mobile", "");
		if (islogin()) { // 已经登录
			tv_nickname.setText(nickname);
			// if(mobile!=null&&mobile.length()>10){
			// mobile = mobile.substring(0, 4)+"****"+mobile.substring(8);
			tv_mobile.setText(mobile);
			// }else{
			// tv_mobile.setText(mobile);
			// }

			ll_logined.setVisibility(View.VISIBLE);
			ll_login.setVisibility(View.GONE);
		} else { // 未登录
			ll_login.setVisibility(View.VISIBLE);
			ll_logined.setVisibility(View.GONE);
		}

	}

	/**
	 * ViewPager适配器
	 * 
	 * @author len
	 * 
	 */
	class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
		public TabPageIndicatorAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// 新建一个Fragment来展示ViewPager item的内容，并传递参数
			fragment = new ItemFragment();
			Bundle args = new Bundle();
			args.putString("muen1_id", parent_list.get(position).id + "");
			args.putInt("muen1_position", position);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLE.get(position % TITLE.size());
		}

		@Override
		public int getCount() {
			return TITLE.size();
		}
	}

	/*********************** 网络请求 ***************************/

	public void initData() {

		HttpUtils http = new HttpUtils();
		String url = Contants.foodIndex;
		http.configCurrentHttpCacheExpiry(0); // 设置缓存时间
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("province", province);
		params.addBodyParameter("city", city); // 必传这2个参数
		params.addBodyParameter("dist", dist); // 必传这2个参数
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.i("MainActivity", result + "");
					Gson gson = new Gson();
					FoodModel foodModel = gson
							.fromJson(result, FoodModel.class);
					Log.i("数据code：", foodModel.code);
					if ("1".equals(foodModel.code)) {
						tvNodata.setVisibility(View.GONE);
						ll_content.setVisibility(View.VISIBLE);
						// 显示通知
						notice_list = foodModel.data.notice_list;
						if (notice_list != null) {
							llTongZhi.setVisibility(View.VISIBLE);
							showNotice();
						} else {
							llTongZhi.setVisibility(View.GONE);
						}
						// 显示1级菜单栏
						parent_list = foodModel.data.parent_list;
						if (parent_list != null) {
							showMenu1();
						}
					} else {
						ll_content.setVisibility(View.GONE);
						tvNodata.setVisibility(View.VISIBLE);
						Toast.makeText(MainActivity.this, "您所选地区暂无商品哟~",
								Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(MainActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	/**
	 * 获取最低消费金额
	 */
	public void getNetMinMoney() {
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(0); // 设置缓存时间
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
							changeLlPay(minMoney);
						} else {
							changeLlPay2();
						}
						// 收货完成操作
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(MainActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	private void changeLlPay(Double minMoney) {
		ll_pay.setBackgroundColor(getResources().getColor(R.color.bg_haicha));
		tvXd.setText("还差" + FoodCarModel.convertDouble(minMoney) + "元");
	}

	private void changeLlPay2() {
		ll_pay.setBackgroundColor(getResources().getColor(R.color.bg_quxisuan));
		tvXd.setText("去结算");
	}

	/**
	 * 获取用户信息 保存 并设置用户信息
	 */
	public void getNetUserInfo() {
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.userinfo;
		http.configCurrentHttpCacheExpiry(0); // 设置缓存时间
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("user_id", usrId);
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("userinfoactivity", result + "");
					Gson gson = new Gson();
					UserInfoBean userInfoBean = gson.fromJson(result,
							UserInfoBean.class);
					if ("1".equals(userInfoBean.code)) {
						UserInfo data = userInfoBean.data;
						// 显示页面
						tv_nickname.setText(data.nickname);
						String mobile = data.mobile;
						if (mobile != null && mobile.length() > 10) {
							// mobile = mobile.substring(0,
							// 4)+"****"+mobile.substring(8);
							tv_mobile.setText(mobile);
						} else {
							tv_mobile.setText(mobile);
						}
						// 保存到文件中
						SharedPreferences userconfig = MainActivity.this
								.getSharedPreferences("userconfig", 0);
						Editor edit = userconfig.edit();
						edit.putString("nickname", data.nickname).commit();
						edit.putString("mobile", data.mobile).commit();

						// 收货完成操作
					}

				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(MainActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	/**
	 * 获取版本信息
	 */
	public void geVerInfo() {
		HttpUtils http = new HttpUtils();
		String url = Contants.UPDATE;
		http.configCurrentHttpCacheExpiry(0); // 设置缓存时间
		RequestParams params = new RequestParams();
		// 添加请求参数
		// params.addBodyParameter("version", CommonUtil.getAppVersionName());
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("userinfoactivity", result + "");
					Gson gson = new Gson();
					GetVerResponse getVerResponse = gson.fromJson(result,
							GetVerResponse.class);
					if ("1".equals(getVerResponse.code)) {
						if (CommonUtil.getAppVerCode() < getVerResponse.data.version_code) {
							AppUtil.doNewVersionUpdate(getVerResponse,
									MainActivity.this);
						}
					}

				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(MainActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	/**
	 * 显示通知
	 */
	private void showNotice() {
		List<TextView> list = new ArrayList<TextView>();
		for (Notice notice : notice_list) {
			TextView textView = new TextView(this);
			textView.setText(notice.content);
			textView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			textView.setGravity(Gravity.LEFT | Gravity.CENTER);
			textView.setTextColor(getResources()
					.getColor(android.R.color.white));
			list.add(textView);
		}
		asvp.setAdapter(new MyPagerAdapter(list));
		// 在onDestory里面停止 滚动
		asvp.startAutoScroll(1000 * 3);
		asvp.setOnPageClickListener(new OnPageClickListener() {
			@Override
			public void onPageClick(AutoScrollViewPager pager, int position) {
				Intent intent = new Intent(MainActivity.this,
						WebViewActivity.class);
				intent.putExtra(WebViewActivity.URL, "http://www.baidu.com");
				startActivity(intent);
			}
		});
	}

	/**
	 * 显示1级菜单栏
	 */
	private void showMenu1() {
		TITLE.clear();
		// 设置数据源
		for (Menu1 menu1 : parent_list) {
			TITLE.add(menu1.name);
		}

		// 初始化 TabPageIndicator
		initfrmg();
	}

	// 计算总价
	public void compute() {
		int sumNum = 0;
		double sunMoney = 0;
		// 获取购物车模型集合
		Map<String, FoodCarModel> carMap = BfApplication.carMap;
		Set<String> keySet = carMap.keySet();
		for (String id : keySet) {
			// 获取模型
			FoodCarModel foodCarModel = carMap.get(id);
			// 获取总代数
			sumNum = sumNum + foodCarModel.daiSumNum;
			sunMoney = sunMoney + foodCarModel.each_bag_money
					* foodCarModel.daiSumNum;
		}
		if (sumNum != 0) {
			shoppingnum.setVisibility(View.VISIBLE);
			tv_sum_money.setText("￥" + FoodCarModel.convertDouble(sunMoney));
		} else {
			shoppingnum.setVisibility(View.GONE);
			tv_sum_money.setText("￥0.00");
		}
		shoppingnum.setText(sumNum + "");
		if (sunMoney < minMoney) {
			changeLlPay(minMoney - sunMoney);
		} else {
			changeLlPay2();
		}
		CarsunMoney = sunMoney;
	}

	/**
	 * 显示购物车 **************购物车模块*******************
	 */
	public void showCar() {
		Map<String, FoodCarModel> carMap = BfApplication.carMap;
		// 判断如果map里面没有元素则 不打开对话框
		if (carMap == null) {
			return;
		}
		if (carMap.keySet().size() <= 0) {
			return;
		}

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
		View mView = carAdapter.getView(0, null, lv_car);
		mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int measuredHeight = mView.getMeasuredHeight();
		int lvHeight = measuredHeight * arrayList.size();
		int qkgucHeight = PxAndDip.dip2px(this, 30);// 清空购物车的高度
		if (lvHeight > metric.heightPixels / 2) {
			pw.setHeight((int) (metric.heightPixels / 2) + qkgucHeight);
		} else {
			pw.setHeight(lvHeight + qkgucHeight);
		}
		pw.setWidth(metric.widthPixels);
		// 产生背景变暗效果 背景一定要在这里设置
		ColorDrawable cd = new ColorDrawable(0x000000);
		pw.setBackgroundDrawable(cd);
		lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		View ll_car = findViewById(R.id.ll_car);
		// View ll_car = findViewById(R.id.iv_car);
		pw.showAtLocation(ll_car, Gravity.BOTTOM, 0, ll_car.getHeight());
	}

	public void setView(View layout) {
		arrayList = new ArrayList<FoodCarModel>();
		Map<String, FoodCarModel> carMap = BfApplication.carMap;
		Set<String> keySetCar = carMap.keySet();

		for (String id : keySetCar) {
			arrayList.add(carMap.get(id));
		}

		lv_car = (ListView) layout.findViewById(R.id.lv_car);
		carAdapter = new CarAdapter();
		if (arrayList != null) {
			lv_car.setAdapter(carAdapter);
		}
		// 清空按钮监听
		TextView tv_del_all = (TextView) layout.findViewById(R.id.tv_del_all);
		tv_del_all.setOnClickListener(new CarAllOnClickListener());

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
			tv_title.setText(foodCarModel.title.trim());
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

	// 点击清空购车

	class CarAllOnClickListener implements OnClickListener {

		public CarAllOnClickListener() {
		}

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
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
			if (foodCarModel == null) {
				Log.e("MainActivity", "foodCarModel null异常   获取的购物车为空");
				return;
			}
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
						// 发送
						EventBus.getDefault().post(
								new EvenBusBean("car_change"));
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
				// 发送
				EventBus.getDefault().post(new EvenBusBean("car_change"));

				break;

			default:
				break;
			}

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				android.os.Process.killProcess(android.os.Process.myPid()); // 获取PID，目前获取自己的也只有该API，否则从/proc中自己的枚举其他进程吧，不过要说明的是，结束其他进程不一定有权限，不然就乱套了。
				System.exit(0); // 常规java、c#的标准退出法，返回值为0代表正常退出
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	// 接口回调方法
	@Override
	public void onArticleSelected(String articleUri) {
		compute();
	}

	// Eventbus回调
	public void onEventMainThread(EvenBusBean event) {

		String msg1 = event.getMsg();
		if ("search_change".equals(msg1)) {
			// 改变总价
			compute();
		}
		if ("reviseNickname".equals(msg1)) {
			// 重新显示用户昵称
			getNetUserInfo();
			setname();
		}
		if ("city".equals(msg1)) {
			// 选择城市并重新调用数据
			province = event.getMsg2(); // mCurrentProviceName
			city = event.getMsg3(); // mCurrentCityName
			dist = event.getMsg4(); // mCurrentCityName
			if (province.contains("北京") || province.contains("天津")
					|| province.contains("上海") || province.contains("重庆")) {
				title_but_right.setText(city);
			} else {
				title_but_right.setText(dist);
			}
			initData();
		}
		if ("clear".equals(msg1)) {
			// 改变总价
			compute();
			tv_sum_money.setText("￥ 0.00");
			// 发送
			EventBus.getDefault().post(new EvenBusBean("car_change"));
		}
	}

	/*
	 * 判断是否在线
	 */
	public boolean islogin() {
		usrId = UserUtil.getUsrId(this);
		if (!"no".equals(usrId)) {
			return true;
		}
		return false;

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

}
