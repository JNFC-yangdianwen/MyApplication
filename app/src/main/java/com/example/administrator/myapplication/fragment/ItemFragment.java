package com.example.administrator.myapplication.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.activity.MainActivity;
import com.example.administrator.myapplication.globle.BfApplication;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.model.FoodCarModel;
import com.example.administrator.myapplication.model.FoodModel;
import com.example.administrator.myapplication.model.FoodModel.FoodData.Menu1.Menu2;
import com.example.administrator.myapplication.model.FoodModel.FoodData.Menu1.Menu2.FoodContent;
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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class ItemFragment extends Fragment {

	private View contextView;
	private ListView lv_menu2;
	private ListView lv_content;
	private Menu2Adapter menu2Adapter;
	private ContentAdapter contentAdapter;
	private String muen1_id;
	private int muen1_position;
	protected List<Menu2> child_list; // 二级菜单集合
	protected List<FoodContent> breed_list; // 内容集合

	int select_menu2 = 0; // 默认 一项
	private String child_id; // 二级菜单的选择id
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private WaitDialog waitDialog;
	private OnArticleSelectedListener mListener;
	int i = 0;
	private TextView tvNoDate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		contextView = inflater
				.inflate(R.layout.fragment_item, container, false);
		// 接收Activity传递过来的信息
		Bundle mBundle = getArguments();
		muen1_id = mBundle.getString("muen1_id");
		muen1_position = mBundle.getInt("muen1_position");
		if (i == 0) {
			// 注册EventBus
			EventBus.getDefault().register(this);
			i = 1;
		}

		waitDialog = new WaitDialog(getActivity());
		initOptions();
		initData();
		select_menu2 = 0;
		// 初始化数据
		initNetData();
		return contextView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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

	public void initData() {

		lv_menu2 = (ListView) contextView.findViewById(R.id.lv_menu2);
		lv_content = (ListView) contextView.findViewById(R.id.lv_content);
		tvNoDate = (TextView) contextView.findViewById(R.id.tvNoDate);

		// 二级标题
		menu2Adapter = new Menu2Adapter();
		// 类容
		contentAdapter = new ContentAdapter();

		// 条目监听
		lv_menu2.setOnItemClickListener(new menu2OnItemClickListener());

	}

	// 二级菜单栏适配器
	class Menu2Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return child_list.size();
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
			View order_itme = getActivity().getLayoutInflater().inflate(
					R.layout.food_menu2_itme, null);
			LinearLayout ll_mneu2_bg = (LinearLayout) order_itme
					.findViewById(R.id.ll_mneu2_bg);
			TextView tv_menu2_title = (TextView) order_itme
					.findViewById(R.id.tv_menu2_title);

			tv_menu2_title.setText(child_list.get(position).name);

			if (select_menu2 == position) {
				select_menu2 = position;
				ll_mneu2_bg.setBackgroundColor(Color.parseColor("#FFFFFF"));
				tv_menu2_title.setTextColor(Color.parseColor("#333333"));
			} else {
				ll_mneu2_bg.setBackgroundColor(Color.parseColor("#00EAEAEA"));
				tv_menu2_title.setTextColor(Color.parseColor("#ffffff"));
			}

			return order_itme;
		}

	}

	// 蔬菜 内容适配器
	class ContentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (breed_list == null) {
				return 0;
			}
			return breed_list.size();
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
			View order_itme = getActivity().getLayoutInflater().inflate(
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
			TextView tvdanwei2 = (TextView) order_itme
					.findViewById(R.id.tvdanwei2);
			View tvgong = order_itme.findViewById(R.id.tvgong);
			View tvjin = order_itme.findViewById(R.id.tvjin);
			FoodContent foodContent = breed_list.get(position);
			tv_title.setText(foodContent.title);
			tv_content.setText(foodContent.content);
			tv_each_bag_money.setText(foodContent.each_bag_money + "元"); // 每袋价格
			tvdanwei.setText("/" + foodContent.each_bag_unit);
			tvdanwei2.setText(foodContent.each_bag_unit);
			tv_pounds.setText(foodContent.poundsunit); // 每袋多少斤
			// if (foodContent.poundsunit == null
			// ||foodContent.poundsunit.equals("")) {
			// tv_jing_num.setVisibility(View.GONE);
			// tvgong.setVisibility(View.GONE);
			// tvjin.setVisibility(View.GONE);
			// }else{
			// tv_jing_num.setVisibility(View.VISIBLE);
			// tvgong.setVisibility(View.VISIBLE);
			// tvjin.setVisibility(View.VISIBLE);
			// }
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
			tv_num.setOnFocusChangeListener(new NumOnFocusChangeListener(
					position, tv_num, tv_dai_num));

			return order_itme;
		}

		// 显示已经拍的宝贝数据量
		private void showNum(int position, TextView tv_num,
				TextView tv_dai_num, View iv_jian) {
			// 获取商品id先和本地购物车模型中的对比 看是否加入过
			FoodContent foodContent = breed_list.get(position);
			String id = foodContent.id;

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

		// 填入数字
		class NumOnFocusChangeListener implements OnFocusChangeListener {
			EditText tv_num;
			int position;
			TextView tv_dai_num;

			public NumOnFocusChangeListener(int position, EditText tv_num,
					TextView tv_dai_num) {
				this.tv_num = tv_num;
				this.position = position;
				this.tv_dai_num = tv_dai_num;
			}

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// 此处为得到焦点时的处理内容
				} else {
					FoodContent foodContent = breed_list.get(position);
					String id = foodContent.id;

					Map<String, FoodCarModel> carMap = BfApplication.carMap;
					FoodCarModel foodCarModel = carMap.get(id);

					// 如果foodCarModel 购物车空了 就直接设置页面数据 为0即可 不往下执行
					if (foodCarModel == null) {
						// 调用宿主activity中的方法 并传递数据
						mListener.onArticleSelected("");

						tv_dai_num.setText("0");
						// tv_jing_num.setText("0");
						tv_num.setText("0");
						return;
					}

					// 此处为失去焦点时的处理内容
					// 当失去焦点时 改变界面 输入的数量不能少于1
					String text = tv_num.getText().toString();
					if (UserUtil.isNumeric(text) && !"".equals(text)) {
						int parseInt = Integer.parseInt(text);
						if (parseInt >= 1) {
							foodCarModel.daiSumNum = parseInt;
							// 调用宿主activity中的方法 并传递数据
							mListener.onArticleSelected("");
						} else {
							tv_num.setText(foodCarModel.daiSumNum + "");

							Toast.makeText(getActivity(), "数量不得少于1",
									Toast.LENGTH_SHORT).show();

						}
					} else {
						tv_num.setText(foodCarModel.daiSumNum + "");
					}

					tv_dai_num.setText(foodCarModel.daiSumNum + "");
					// tv_jing_num.setText(foodCarModel.pounds*foodCarModel.daiSumNum+"");

				}

			}

		}

		// 添加删除宝贝
		class CarJiaJianOnClickListener implements OnClickListener {
			int position;
			TextView tv_num;
			TextView tv_dai_num;
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
				FoodContent foodContent = breed_list.get(position);
				String id = foodContent.id;

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
						// 调用宿主activity中的方法 并传递数据
						mListener.onArticleSelected("");
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

						// 调用宿主activity中的方法 并传递数据
						mListener.onArticleSelected("");

						break;

					default:
						break;
					}
				} else { // 未加入 只有按加按钮 才会走这里 添加第一商品
					FoodCarModel newfoodCarModel = new FoodCarModel();
					newfoodCarModel.id = foodContent.id;
					newfoodCarModel.title = foodContent.title;
					newfoodCarModel.each_bag_money = Double
							.parseDouble(foodContent.each_bag_money); // 每袋单价
					newfoodCarModel.daiSumNum = 1; // 总代数
					// if
					// (foodContent.poundsunit!=null&&!foodContent.poundsunit.equals(""))
					// {
					// String jin = foodContent.poundsunit.substring(0,
					// foodContent.poundsunit.indexOf("斤"));
					// newfoodCarModel.pounds = Integer.parseInt(jin); //每袋斤数sz
					// }
					carMap.put(id, newfoodCarModel);
					iv_jian.setVisibility(View.VISIBLE);
					tv_num.setVisibility(View.VISIBLE);
					tv_num.setText("1");
					tv_dai_num.setText("1");
					// tv_jing_num.setText(newfoodCarModel.pounds*newfoodCarModel.daiSumNum+"");
					// 调用宿主activity中的方法 并传递数据
					mListener.onArticleSelected("");

				}

			}

		}

	}

	// 2级菜单栏监听
	class menu2OnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 记录选择的2级标题栏
			select_menu2 = position;
			child_id = child_list.get(position).id;
			initNetContent();
		}

	}

	/*********************** 网络请求 ***************************/

	public void initNetData() {

		waitDialog.show();
		HttpUtils http = new HttpUtils();
		String url = Contants.foodIndex;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("province", MainActivity.province);
		params.addBodyParameter("city", MainActivity.city); // 必传这2个参数
		params.addBodyParameter("dist", MainActivity.dist); // 必传这2个参数
		params.addBodyParameter("parent_id", muen1_id); // 大分类
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {

					String result = responseInfo.result;
					Log.e("initNetData", result + "");
					Gson gson = new Gson();
					FoodModel foodModel = gson
							.fromJson(result, FoodModel.class);
					if ("1".equals(foodModel.code)) {
						Log.v("muen1_position", muen1_position + "");
						child_list = foodModel.data.parent_list
								.get(muen1_position).child_list; // 二级菜单目录
						if (child_list != null) {
							lv_menu2.setAdapter(menu2Adapter);
							Menu2 menu2 = child_list.get(0);
							breed_list = menu2.breed_list;
							if (breed_list != null && breed_list.size() > 0) {
								lv_content.setVisibility(View.VISIBLE);
								lv_content.setAdapter(contentAdapter);
							} else {
								lv_content.setVisibility(View.GONE);
								tvNoDate.setVisibility(View.VISIBLE);
							}
						}
					} else {
						Toast.makeText(getActivity(), "服务器异常，请稍后再试",
								Toast.LENGTH_SHORT).show();
					}
				}

				waitDialog.dismiss();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(getActivity(), "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
				waitDialog.dismiss();
			}

		});
	}

	public void initNetContent() {
		waitDialog.show();
		HttpUtils http = new HttpUtils();
		String url = Contants.foodIndex;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("province", MainActivity.province);
		params.addBodyParameter("city", MainActivity.city); // 必传这2个参数
		params.addBodyParameter("dist", MainActivity.dist); // 必传这2个参数
		params.addBodyParameter("parent_id", muen1_id); // 大分类
		params.addBodyParameter("child_id", child_id); // 小分类

		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("initNetContent", result + "");
					Gson gson = new Gson();
					FoodModel foodModel = gson
							.fromJson(result, FoodModel.class);
					if ("1".equals(foodModel.code)) {
						child_list = foodModel.data.parent_list
								.get(muen1_position).child_list; // 二级菜单目录
						if (child_list != null) {
							lv_menu2.setAdapter(menu2Adapter);

							Menu2 menu2 = child_list.get(select_menu2);
							breed_list = menu2.breed_list;
							if (breed_list != null && breed_list.size() > 0) {
								lv_content.setVisibility(View.VISIBLE);
								tvNoDate.setVisibility(View.GONE);
								lv_content.setAdapter(contentAdapter);
							} else {
								lv_content.setVisibility(View.GONE);
								tvNoDate.setVisibility(View.VISIBLE);
							}
						}
					} else {
						Toast.makeText(getActivity(), "服务器异常，请稍后再试",
								Toast.LENGTH_SHORT).show();
					}
				}

				waitDialog.dismiss();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(getActivity(), "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
				waitDialog.dismiss();
			}

		});
	}

	// 接口方法 让activity去实现这个接口
	public interface OnArticleSelectedListener {
		public void onArticleSelected(String articleUri);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			// 利用多态 （获取到这个对象再 调用这个对象的方法）
			mListener = (OnArticleSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	// Eventbus回调
	public void onEventMainThread(EvenBusBean event) {

		String msg1 = event.getMsg();
		if ("car_change".equals(msg1)) {
			contentAdapter.notifyDataSetChanged();
		}
	}

	// 解决activity
	// nDetach阶段fragment已经与activity脱离关系即fragment持有的activity对象已被置null，而onDestroyView阶段fragment中仍然保留与activity之间的关系,此时fragment持有的activity对象仍然有效
	// 的问题
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}