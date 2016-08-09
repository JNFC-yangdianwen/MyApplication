package com.example.administrator.myapplication.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.example.administrator.myapplication.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.buyfood.R;
import com.example.administrator.myapplication.globle.BfApplication;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.view.widget.XListView;
import com.example.administrator.myapplication.view.widget.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 根据传入的地点 和POI展示 poi列表，可以切换地图模式 百度地图获取地址页面
 * 
 * @author
 * 
 */
public class PoiListActivity extends BaseActivity implements
		OnGetPoiSearchResultListener, OnClickListener, IXListViewListener {

	private TextView tv_title;
	private Button btn_poi_show;
	private XListView xlv_poi_list;
	private RelativeLayout rl_loading;
	// 目标地点经纬度
	private double latitude, longitude;
	// 查询关键词
	private String keyWord;

	private PoiSearch mPoiSearch;
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	private static boolean isEnd = true;// 加载是否结束
	// 限制显示地图结果 还是列表结果
	private boolean showInMap = false;
	// 记录查询记录页数
	private int pageIndex = 0;
	// 查询结果
	private PoiResult result;
	// POI查询结果列表
	public List<PoiInfo> poiList = new ArrayList<PoiInfo>();
	private int pageNum;// 记录结果条数
	private PoiAdapter adapter;
	private EditText et_text;
	private Button bt_sch;
	private EditText addr_text;
	private Button bt_sub;
	public String selsctAddr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("地图选择地址");
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		initView();
		initData();
		setListener();

	}

	private void initView() {
		setContentView(R.layout.activity_poi_list);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_poi_show = (Button) findViewById(R.id.btn_poi_show);
		xlv_poi_list = (XListView) findViewById(R.id.xlv_poi_list);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		mMapView = (MapView) findViewById(R.id.bd_mapview);
		et_text = (EditText) findViewById(R.id.et_text);
		bt_sch = (Button) findViewById(R.id.bt_sch);
		addr_text = (EditText) findViewById(R.id.addr_text);
		// 提交地址
		bt_sub = (Button) findViewById(R.id.bt_sub);

		xlv_poi_list.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);

		bt_sub.setOnClickListener(this);

	}

	private void initData() {
		mBaiduMap = mMapView.getMap();
		// Intent intent = getIntent();
		// latitude = intent.getDoubleExtra("latitude", 0.0);
		// longitude = intent.getDoubleExtra("longitude", 0.0);
		latitude = BfApplication.latitude;
		longitude = BfApplication.longitude;
		// Toast.makeText(this, latitude+"---"+longitude,
		// Toast.LENGTH_SHORT).show();
		keyWord = BfApplication.addr;
		tv_title.setText(keyWord);
		System.out.println("latitude:" + latitude + "--longitude:" + longitude
				+ "--keyWord:" + keyWord);
		getPoiList(pageIndex);
		// 显示获取地址列表
		showPoiList();

	}

	private void setListener() {
		btn_poi_show.setOnClickListener(this);
		xlv_poi_list.setXListViewListener(this);
		xlv_poi_list.setPullRefreshEnable(false);
		xlv_poi_list.setPullLoadEnable(true);
		View view = View.inflate(PoiListActivity.this, R.layout.xlv_footer,
				null);
		xlv_poi_list.addFooterView(view);
		// 搜索时 刷新界面
		bt_sch.setOnClickListener(this);
	}

	/**
	 * 查询POI
	 */
	private void getPoiList(int pageIndex) {
		isEnd = false;
		LatLng ll = new LatLng(latitude, longitude);
		// mPoiSearch.searchPoiDetail(new
		// PoiNearbySearchOption().keyword(keyWord)
		// .pageNum(pageIndex));
		mPoiSearch.searchNearby(new PoiNearbySearchOption().location(ll)
				.keyword(keyWord).radius(10000).pageNum(pageIndex));
	}

	/**
	 * 根据城市查询POI
	 */
	private void getCityPoiList(int pageIndex) {
		isEnd = false;
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(BfApplication.province).keyword(keyWord)
				.pageNum(pageIndex));
	}

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(PoiListActivity.this, "未找到结果", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (result.getTotalPageNum() == 0) {
			Toast.makeText(PoiListActivity.this, "未找到结果", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		this.result = result;
		// 显示地图标注
		showPoiInMap();
		pageNum = result.getTotalPageNum();
		poiList = result.getAllPoi();
		xlv_poi_list.stopLoadMore();// 停止加载
		adapter.notifyDataSetChanged();
		if (poiList.size() > 0) {// 显示列表
			rl_loading.setVisibility(View.GONE);
			xlv_poi_list.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(PoiListActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(PoiListActivity.this,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

	}

	/**
	 * POI数据适配器
	 * 
	 * @author sf
	 * 
	 */
	private class PoiAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return poiList.size();
		}

		@Override
		public PoiInfo getItem(int position) {
			return poiList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.poi_list_item, null);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.tv_address = (TextView) convertView
						.findViewById(R.id.tv_address);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			PoiInfo info = getItem(position);

			System.out.println("POIINFO:" + info.address + "--" + info.city
					+ "--" + info.name + "--" + info.phoneNum + "--"
					+ info.postCode + "--" + info.uid + "--" + info.isPano);

			holder.tv_name.setText((position + 1) + "." + info.name);
			holder.tv_address.setText(info.address);

			// if(position == poiList.size()-1){
			// // 地图显示位置
			// showPoiInMap();
			// }
			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_name;
		TextView tv_address;
	}

	/**
	 * POI数据监听器
	 */
	public class PoiOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg2 - 1 >= 0) {
				// 把选择的位置显示上去
				PoiInfo poiInfo = poiList.get(arg2 - 1);
				selsctAddr = poiInfo.name;
				et_text.setText(selsctAddr);
				EventBus.getDefault().post(
						new EvenBusBean("addr_map_change", selsctAddr));
				finish();
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_poi_show:
			changeShowMode();
			break;
		case R.id.bt_sch: // 搜索新地址
			searchAddr();
			break;
		case R.id.bt_sub: // 提交地址
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 搜索刷新界面
	 */

	private void searchAddr() {
		String text = et_text.getText().toString();
		if (text != null && !"".equals(text)) {
			// latitude = 0.0;
			// longitude = 0.0;
			keyWord = text;
			getPoiList2(text);
			poiList.clear();
		} else {
			Toast.makeText(this, "亲 您又逗比了，条件不能为空哟", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 搜索时查询POI
	 */
	private void getPoiList2(String text) {
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(BfApplication.province).keyword(text).pageNum(1));
	}

	/**
	 * 在地图上展示POI
	 */
	private void changeShowMode() {
		showInMap = !showInMap;
		if (showInMap) {
			btn_poi_show.setText("列表");
			xlv_poi_list.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);
			showPoiInMap();
		} else {
			btn_poi_show.setText("地图");
			mMapView.setVisibility(View.GONE);
			xlv_poi_list.setVisibility(View.VISIBLE);
			showPoiList();
		}

	}

	/**
	 * 显示POI列表
	 */
	private void showPoiList() {
		adapter = new PoiAdapter();
		xlv_poi_list.setAdapter(adapter);
		xlv_poi_list.setOnItemClickListener(new PoiOnItemClickListener());
	}

	/**
	 * 地图展示POI查询结果
	 */
	private void showPoiInMap() {
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiVoerlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
	}

	class MyPoiVoerlay extends PoiOverlay {

		public MyPoiVoerlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		if (pageNum - 1 == pageIndex) {
			Toast.makeText(PoiListActivity.this, "暂无更多数据!", Toast.LENGTH_SHORT)
					.show();
			xlv_poi_list.stopLoadMore();
			return;
		}

		pageIndex++;
		getCityPoiList(pageIndex);
		poiList.clear();
		rl_loading.setVisibility(View.VISIBLE);
		xlv_poi_list.setVisibility(View.GONE);

		// 根据地点查询的方式
		// if (pageNum - 1 == pageIndex) {
		// Toast.makeText(PoiListActivity.this, "暂无更多数据!", Toast.LENGTH_SHORT)
		// .show();
		// return;
		// }
		// pageIndex++;
		// if(keyWord.toString().trim().equals("")){
		//
		// mPoiSearch.searchInCity((new
		// PoiCitySearchOption()).city(BfApplication.province).keyword(keyWord).pageNum(pageIndex));
		// }
		// mPoiSearch.searchInCity((new
		// PoiCitySearchOption()).city(BfApplication.province).keyword(keyWord).pageNum(pageIndex));
		// xlv_poi_list.stopLoadMore();// 停止加载

	}

	// @Override
	// public void onFooterLoad(AbPullToRefreshView view) {
	// if (pageNum - 1 == load_Index) {
	// Toast.makeText(MapActivity.this, "暂无更多数据!", Toast.LENGTH_SHORT)
	// .show();
	// return;
	// }
	// Toast.makeText(MapActivity.this, "加载更多数据!", Toast.LENGTH_SHORT)
	// .show();
	// load_Index++;
	// if(keyWorldsView.getText().toString().trim().equals("")){
	//
	// mpoiSearch.searchInCity((new
	// PoiCitySearchOption()).city(city).keyword(addrStr).pageNum(load_Index));
	// }
	// mpoiSearch.searchInCity((new
	// PoiCitySearchOption()).city(province).keyword(keyWorldsView.getText().toString()).pageNum(load_Index));
	// abPullToRefreshView.onFooterLoadFinish();
	//
	// }

}
