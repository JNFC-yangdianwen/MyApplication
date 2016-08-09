package com.example.administrator.myapplication.activity;

import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.alipay.PayDemoActivity;
import com.example.administrator.myapplication.globle.BfApplication;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.model.FirstEvent;
import com.example.administrator.myapplication.model.FoodCarModel;
import com.example.administrator.myapplication.model.MsgBean;
import com.example.administrator.myapplication.model.OrderListBean;
import com.example.administrator.myapplication.model.OrderVo;
import com.example.administrator.myapplication.model.RepeatOrderBean;
import com.example.administrator.myapplication.model.RepeatOrderBean.RepeatOrderinfo.RepeatOrder;
import com.example.administrator.myapplication.utils.UserUtil;
import com.example.administrator.myapplication.view.PayDialog;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import de.greenrobot.event.EventBus;

/**
 * 我的订单页面
 * 
 * @author fengzi
 * 
 */
public class OrderListActivity extends BaseActivity {

	private PullToRefreshListView pullListView;
	public int pageint = 1;
	public boolean isRefershing;
	public boolean isLoadMore;
	private OrderAdapter orderAdapter;
	protected List<OrderVo> orderData;
	private IWXAPI api;
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

	private String orderid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("我的订单");
		templateButtonRight.setVisibility(View.VISIBLE);
		templateButtonRight.setImageResource(R.drawable.wddd_tel);
		setContentView(R.layout.activity_orderlist);

		pullListView = (PullToRefreshListView) findViewById(R.id.chat_lv);
		// 下拉刷新 上啦加载配置
		pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		// 初始化数据
		initData();

		OnRefersh refersh = new OnRefersh();
		pullListView.setOnRefreshListener(refersh);

		orderAdapter = new OrderAdapter();
		pullListView.setOnItemClickListener(new OrderOnItemClickListener());
		templateButtonRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder bulider = new AlertDialog.Builder(
						OrderListActivity.this);
				bulider.setMessage("拨打" + getString(R.string.phone) + "?");
				bulider.setPositiveButton("拨打",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Intent intent5 = new Intent(Intent.ACTION_CALL,
										Uri.parse("tel:"
												+ getString(R.string.phone)));
								startActivity(intent5);
							}
						});
				bulider.setNegativeButton("取消", null);
				bulider.show();
			}
		});
		EventBus.getDefault().register(this);
		regToWx();
	}

	public void onEventMainThread(FirstEvent event) {
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 
	 * @author czz
	 * @createdate 2015年11月27日 上午9:42:46
	 * @Description: (注册微信)
	 * 
	 */
	private void regToWx() {
		api = WXAPIFactory.createWXAPI(this, Contants.APP_ID, false);
		api.registerApp(Contants.APP_ID);
	}

	// 上拉下加载
	class OnRefersh implements PullToRefreshBase.OnRefreshListener2<ListView> {

		/**
		 * 下拉
		 * 
		 * @param refreshView
		 */
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			if (pageint != 0) {
				pageint = 1;
				isRefershing = true;
				initData();
			}
		}

		/**
		 * 上拉
		 * 
		 * @param refreshView
		 */
		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			isLoadMore = true;
			pageint += 1;

			Toast.makeText(OrderListActivity.this, "没有更多数据", Toast.LENGTH_SHORT)
					.show();
			pullListView.onRefreshComplete();
		}

	}

	class OrderAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (orderData == null) {
				return 0;
			}
			return orderData.size();
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
			View view = getLayoutInflater().inflate(R.layout.order_itme, null);
			TextView tv_ordernum = (TextView) view
					.findViewById(R.id.tv_ordernum); // 订单号
			TextView tv_title_arr = (TextView) view
					.findViewById(R.id.tv_title_arr); // 名称
			TextView tv_money = (TextView) view.findViewById(R.id.tv_money); // 金额
			TextView tv_order_time = (TextView) view
					.findViewById(R.id.tv_order_time); // 时间
			TextView tv_status = (TextView) view.findViewById(R.id.tv_status); // 订单状态
			TextView tv_sh = (TextView) view.findViewById(R.id.tv_sh); // 收货状态
			TextView tv_cancle = (TextView) view.findViewById(R.id.tv_cancle); // 取消订单

			ImageView iv_del = (ImageView) view.findViewById(R.id.iv_del); // 删除
			TextView tv_zlyd = (TextView) view.findViewById(R.id.tv_zlyd); // 再来一单
			final OrderVo order = orderData.get(position);

			tv_ordernum.setText(order.ordernum);
			if (order.goods_num == 1) {
				tv_title_arr.setText(order.title_arr + "等" + order.goods_num
						+ "件商品");
			} else {
				tv_title_arr.setText(order.title_arr + "等" + order.goods_num
						+ "件商品");
			}

			tv_money.setText("￥"
					+ FoodCarModel.convertDouble(Double
							.parseDouble(order.money)));
			tv_order_time.setText(order.order_time);
			// 订单状态
			// status 支付状态(0代表订单取消,1代表商家已接单,2代表已支付已确认收货) 注意： 取消只能是后台取消
			String orderStatus = order.status;
			if ("0".equals(orderStatus)) { // 等待付款
				tv_status.setText("等待付款"); // 文字状态
				tv_sh.setVisibility(View.VISIBLE); // 按钮
				tv_cancle.setVisibility(View.VISIBLE); // 按钮
				tv_sh.setText("去付款");
				tv_sh.setOnClickListener(new ReceiveOnClickListener(order, "0")); // 付款
				tv_cancle.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder = new AlertDialog.Builder(
								OrderListActivity.this);
						builder.setMessage("确认取消此订单么？");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										PayFail(order.ordernum); // 取消订单
										delOrder2(order.order_id);
									}
								});
						builder.setNegativeButton("取消", null);
						builder.show();
					}
				});
				iv_del.setEnabled(false);
				iv_del.setVisibility(View.VISIBLE); // 收货完成后显示删除按钮
				iv_del.setOnClickListener(new ReceiveOnClickListener(order, "")); // 收货
			} else if ("1".equals(orderStatus)) { // 付款完了等待收货
				tv_status.setText("商家已接单（在线支付）");
				iv_del.setEnabled(true);
				tv_sh.setVisibility(View.VISIBLE);
				tv_sh.setText("确认收货");
				tv_sh.setOnClickListener(new ReceiveOnClickListener(order, "1")); // 收货

			} else if ("2".equals(orderStatus)) { // 已完成
				iv_del.setVisibility(View.VISIBLE); // 收货完成后显示删除按钮
				iv_del.setOnClickListener(new ReceiveOnClickListener(order, "")); // 删除
				tv_status.setText("已完成");
				iv_del.setEnabled(true);
			} else if ("3".equals(orderStatus)) { // 货到付款
				tv_status.setText("商家已接单（货到付款）");
				iv_del.setEnabled(true);
				tv_sh.setVisibility(View.VISIBLE);
				tv_sh.setText("确认收货");
				tv_sh.setOnClickListener(new ReceiveOnClickListener(order, "1")); // 收货

			}
			// 再来一单
			tv_zlyd.setOnClickListener(new ReceiveOnClickListener(order, ""));

			return view;
		}

	}

	// 付款 或收货按钮监听 删除 再来一单
	class ReceiveOnClickListener implements OnClickListener {
		OrderVo order;
		String status;

		public ReceiveOnClickListener(OrderVo order, String status) {
			this.order = order;
			this.status = status;
			orderid = order.ordernum;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_sh: // 付款 收货按钮
				if ("0".equals(status)) { // 付款
					final PayDialog payDialog = new PayDialog(
							OrderListActivity.this);
					View tv_cancel = payDialog.tv_cancel;
					tv_cancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							payDialog.dismiss();
						}
					});
					View llAlipay = payDialog.findViewById(R.id.ll_alipay);
					View llWechatpay = payDialog
							.findViewById(R.id.ll_wechatpay);
					llAlipay.setOnClickListener(new OnClickListener() { // 支付宝支付
						@Override
						public void onClick(View arg0) {
							new PayDemoActivity(OrderListActivity.this,
									order.title_arr, "精菜", order.money,
									order.ordernum).check();
						}
					});
					llWechatpay.setOnClickListener(new OnClickListener() {// 微信支付
								@Override
								public void onClick(View arg0) {
									doWxPayById(order.ordernum, order.money);
								}
							});
					payDialog.show();
				} else if ("1".equals(status)) { // 收货
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							OrderListActivity.this);
					dialog.setTitle("请确认");
					dialog.setMessage("确认您的物品是否收到？");
					dialog.setNegativeButton("没收到", null);
					dialog.setPositiveButton("已收到",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									receiveOrder(order.order_id);
								}
							});
					dialog.show();
				}

				break;
			case R.id.iv_del: // 删除已完成的订单
				AlertDialog.Builder builder = new AlertDialog.Builder(
						OrderListActivity.this);
				builder.setMessage("确认删除此订单么？");
				builder.setPositiveButton("删除",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								delOrder(order.order_id);
							}
						});
				builder.setNegativeButton("取消", null);
				builder.show();
				break;

			case R.id.tv_zlyd: // 再来一单
				repeatOrder(order.order_id);
				break;

			default:
				break;
			}
		}

	}

	// 条目监听 进入到详情页面
	class OrderOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			OrderVo order = orderData.get(arg2 - 1);
			Intent intent = new Intent(OrderListActivity.this,
					OrderInfoActivity.class);
			intent.putExtra("order_id", order.order_id);
			OrderListActivity.this.startActivity(intent);

		}

	}

	/*********************** 网络请求 ***************************/
	/**
	 * 获取订单
	 */
	public void initData() {
		waitDialog.show();
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.getOrderList;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("user_id", usrId);
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// if(isRefershing){
				// 停止刷新
				pullListView.onRefreshComplete();
				// }

				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("OrderListActivity", result + "");
					Gson gson = new Gson();
					OrderListBean orderBean = gson.fromJson(result,
							OrderListBean.class);
					waitDialog.dismiss();
					if ("1".equals(orderBean.code)) {
						orderData = orderBean.data;
						pullListView.setAdapter(orderAdapter);
					} else {
						if (orderData != null && orderAdapter != null) {
							orderData.clear();
							orderAdapter.notifyDataSetChanged();
						}

						Toast.makeText(OrderListActivity.this, "暂无订单",
								Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				if (isRefershing) {
					// 停止刷新
					pullListView.onRefreshComplete();
				}
				Toast.makeText(OrderListActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 确认收货订单
	 */
	public void receiveOrder(String order_id) {
		waitDialog.show();
		HttpUtils http = new HttpUtils();
		String url = Contants.orderReceive;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("order_id", order_id);
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("OrderListActivity", result + "");
					Gson gson = new Gson();
					MsgBean msgBean = gson.fromJson(result, MsgBean.class);
					waitDialog.dismiss();
					if ("1".equals(msgBean.code)) {
						// 收货完成操作
						initData();
					} else {
						Toast.makeText(OrderListActivity.this,
								msgBean.msg + "", Toast.LENGTH_SHORT).show();
					}

				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				Toast.makeText(OrderListActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	/**
	 * 删除已完成的订单
	 */
	public void delOrder(String order_id) {
		waitDialog.show();
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.orderDel;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("order_id", order_id);
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("OrderListActivity", result + "");
					Gson gson = new Gson();
					MsgBean msgBean = gson.fromJson(result, MsgBean.class);
					waitDialog.dismiss();
					if ("1".equals(msgBean.code)) {
						// 收货完成操作
						initData();
						Toast.makeText(OrderListActivity.this, "删除成功",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(OrderListActivity.this,
								msgBean.msg + "", Toast.LENGTH_SHORT).show();
					}

				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				Toast.makeText(OrderListActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	/**
	 * 删除已完成的订单
	 */
	public void delOrder2(String order_id) {
		waitDialog.show();
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.orderDel;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("order_id", order_id);
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("OrderListActivity", result + "");
					Gson gson = new Gson();
					MsgBean msgBean = gson.fromJson(result, MsgBean.class);
					waitDialog.dismiss();
					if ("1".equals(msgBean.code)) {
						// 收货完成操作
						initData();
					} else {
						Toast.makeText(OrderListActivity.this,
								msgBean.msg + "", Toast.LENGTH_SHORT).show();
					}

				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				Toast.makeText(OrderListActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	/**
	 * 再来一单
	 */
	public void repeatOrder(String order_id) {
		waitDialog.show();
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.orderRepeat;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("order_id", order_id);
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("OrderListActivity", result + "");
					Gson gson = new Gson();
					RepeatOrderBean repeatOrderBean = gson.fromJson(result,
							RepeatOrderBean.class);
					waitDialog.dismiss();
					if ("1".equals(repeatOrderBean.code)) {
						// Toast.makeText(OrderListActivity.this,
						// repeatOrderBean.msg+"", Toast.LENGTH_SHORT).show();
						List<RepeatOrder> order_assort = repeatOrderBean.data.order_assort;
						BfApplication.carMap.clear();
						for (RepeatOrder repeatOrder : order_assort) {
							/****************************** 注意 这里模型类型改了必须改接收类型 ************************************************************/
							FoodCarModel foodCarModel = new FoodCarModel();
							int number = Integer.parseInt(repeatOrder.number); // 总代数
							double sum_pounds = repeatOrder.sum_pounds; // 总斤数
							double sum_money = Double
									.parseDouble(repeatOrder.sum_money); // 总金额

							foodCarModel.title = repeatOrder.title; // 名称
							foodCarModel.id = repeatOrder.goods_id; // id
							foodCarModel.daiSumNum = number; // 总代数
							foodCarModel.each_bag_money = sum_money / number; // 每袋价格
							foodCarModel.pounds = sum_pounds / number; // 每袋价格
							BfApplication.carMap.put(repeatOrder.goods_id,
									foodCarModel);
							// 成功后 改变购物车 跳转支付页面
							EventBus.getDefault().post(
									new EvenBusBean("search_change"));
							Intent intent = new Intent(OrderListActivity.this,
									OrderConfirmActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							OrderListActivity.this.startActivity(intent);
							OrderListActivity.this.finish();
						}
						// 收货完成操作
					} else {
						Toast.makeText(OrderListActivity.this,
								repeatOrderBean.msg + "", Toast.LENGTH_SHORT)
								.show();
					}

				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				Toast.makeText(OrderListActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
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
										Toast.makeText(OrderListActivity.this,
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
						Toast.makeText(OrderListActivity.this, "网络加载异常，请稍后再试",
								Toast.LENGTH_SHORT).show();
					}

				});
	}

	/**
	 * 
	 * @author gc
	 * @createdate 2015年11月11日 下午5:44:57
	 * @Description: (支付失败接口方法)
	 * 
	 */
	private void PayFail(String product_id) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("正在加载");
		pd.show();
		RequestParams params = new RequestParams();
		params.addBodyParameter("orderno", product_id);
		Log.v("orderid", product_id);
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, Contants.PAYFAIL, params,
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
								showShortToastMessage("取消成功");
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
						Toast.makeText(OrderListActivity.this, "网络加载异常，请稍后再试",
								Toast.LENGTH_SHORT).show();
					}

				});
	}
}