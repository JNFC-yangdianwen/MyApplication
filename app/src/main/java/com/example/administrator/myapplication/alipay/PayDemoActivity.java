package com.example.administrator.myapplication.alipay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.example.administrator.myapplication.activity.OrderListActivity;
import com.example.administrator.myapplication.model.FirstEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.greenrobot.event.EventBus;

public class PayDemoActivity {

	// 商户PID
	public static final String PARTNER = "2088121493071293";//
	// 商户收款账号
	public static final String SELLER = "jinanydwl@163.com";//
	// 商户私钥，pkcs8格式
	public static String RSA_PRIVATE = "MIICXAIBAAKBgQC7Yc8CbPbFC/2Mq6fYORYQAy4OHj26H1e1uug6RM42qq0bi5Wv2lodvddAJjaAurtOJU6tF0BKRwrBjzsYxCo1N/q2ZSHQFD0KQoKIzA3prwpLR6iWrngk+dav9ktZ+ca7BMZ7VdYUfavCNc9fkOhvHE781Out0qgR5x2a2duHNwIDAQABAoGAZTwnigjynWyn9Gr9CF13JPmC1U/TS44JZf5v3qMzweceFSapnVNbonvhIiBDC/NWpJaVVgCc1ERjdEV8yRmaLy79NqjmOeyqpvZiR/0SLCD4648fc+7zPJdB2TW4lrY8RcedA5xYBscJytW8BOBWQ4PAR4sCGF9K5X9VxREMYDkCQQDtYAW3XFOnb49PHm+wSWti44m+R+evTM6BBw+q97yaNCxGg47rcp31hEZE4QTlSYI7kyXjwFf/hKZ7qAubvNL1AkEAyhWdDEc/dBRNh8Km7SzHyp8xZvCqYYcfEgt/Am0GmmIUNNpQsiwzn38X77IdadqTWnLVhdqZmRITa1HlshFN+wJAOyH+ioz/ceiCpM0KObowfDP+rl4vQyY5Ez91EbwhSbDRjsdbJSAqZW0MLEB5/bxwKvMfLztNMKQU7R0Rtzw0uQJBAL/Q+iwQaOOG7fyiQA46hj4HEz5xzFgciNp4/QrRcAYUeLeeTiCBIlMPsSm8+Hxkk5+0Bocycr8/0Bz7NLrsAAcCQAV33svHL8unCCuKzht6IiIbPbPWQGM2dllHflEALGu7rNdYIlj3Z1TOxb8m4WmRHJcP8rv69rp4lanfeVoDuXs=";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "";
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private Context context;
	private String wareName; // 商品名称
	private String wareMoney;// 商品价格
	private String orderId;// 商品id
	private String wareDetail;// 商品详情

	public PayDemoActivity(Context context, String wareName, String wareDetail,
			String wareMoney, String orderId) {
		this.context = context;
		this.wareMoney = wareMoney;
		this.wareName = wareName;
		this.orderId = orderId;
		this.wareDetail = wareDetail;

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
					Intent intent1 = new Intent(context,
							OrderListActivity.class);
					context.startActivity(intent1);
					// 支付成功更新页面
					EventBus.getDefault().post(new FirstEvent(0));
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT)
								.show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				boolean flag = (Boolean) msg.obj;
				if (flag) {
					pay();
				} else {
					Toast.makeText(context, "支付宝认证账户不存在", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			}
			default:
				break;
			}
		};
	};

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay() {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
				|| TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(context)
					.setTitle("警告")
					.setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
								}
							}).show();
			return;
		}
		// 订单
		String orderInfo = getOrderInfo();

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask((Activity) context);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask((Activity) context);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask((Activity) context);
		String version = payTask.getVersion();
		Toast.makeText(context, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo() {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + wareName + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + wareDetail + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + wareMoney + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ "http://t13.beauityworld.com/VegetablePayAPI/doPayMoney"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
