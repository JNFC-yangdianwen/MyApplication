package com.example.administrator.myapplication.globle;

public class Contants {
	/**
	 * 前缀
	 */
	public final static String GLOBAL_URL = "http://app.jingcaibuy.com";

	/**
	 * 首页数据接口
	 */
	public final static String foodIndex = GLOBAL_URL
			+ "/VegetableUserAPI/getVegetableInfoByArea";
	/**
	 * 商品搜索
	 */
	public final static String searchGoods = GLOBAL_URL
			+ "/VegetableUserAPI/getSearchVegetableListByName";
	/**
	 * 获取地址
	 */
	public final static String getAdds = GLOBAL_URL
			+ "/VegetableUserAPI/getUserAddressListByUid";
	/**
	 * 修改地址
	 */
	public final static String reviseAdds = GLOBAL_URL
			+ "/VegetableUserAPI/doUserSaveAressById";
	/**
	 * 删除地址
	 */
	public final static String deleAdds = GLOBAL_URL
			+ "/VegetableUserAPI/doDeleteUserAddressById";
	/**
	 * 新建地址
	 */
	public final static String newAddr = GLOBAL_URL
			+ "/VegetableUserAPI/doUserAddAressById";
	/**
	 * 优惠劵
	 */
	public final static String couponList = GLOBAL_URL
			+ "/VegetableUserAPI/getMyCouponList";
	/**
	 * 送货时间
	 */
	public final static String sendTimeList = GLOBAL_URL
			+ "/VegetablePayAPI/getDurationList";
	/**
	 * 下订单 获取订单号
	 */
	public final static String getOrder = GLOBAL_URL
			+ "/VegetablePayAPI/doVegetableUserOrderById";
	/**
	 * 获取我的订单列表
	 */
	public final static String getOrderList = GLOBAL_URL
			+ "/VegetablePayAPI/getMyOrderListById";
	/**
	 * 确认收货
	 */
	public final static String orderReceive = GLOBAL_URL
			+ "/VegetablePayAPI/doConfirmReceiptById";
	/**
	 * 删除已完成的订单
	 */
	public final static String orderDel = GLOBAL_URL
			+ "/VegetablePayAPI/doDelOrderById";
	/**
	 * 再来一单
	 */
	public final static String orderRepeat = GLOBAL_URL
			+ "/VegetablePayAPI/doComeAgainOrderById";
	/**
	 * 订单详情
	 */
	public final static String orderInof = GLOBAL_URL
			+ "/VegetablePayAPI/getOrderInfoById";
	/**
	 * 最低消费金额
	 */
	public final static String lowest = GLOBAL_URL
			+ "/VegetableUserAPI/getMinMoneyInfo";
	/**
	 * 个人信息
	 */
	public final static String userinfo = GLOBAL_URL
			+ "/VegetableUserAPI/getUserInfoById";
	/**
	 * 个人信息
	 */
	public final static String saveUserInfo = GLOBAL_URL
			+ "/VegetableUserAPI/doSaveUserInfoById";
	/**
	 * 获取邀请码和活动
	 */
	public final static String getCode = GLOBAL_URL
			+ "/VegetableUserAPI/getMyInviteInfoById";
	/**
	 * 意见反馈
	 */
	public final static String suggest = GLOBAL_URL
			+ "/VegetableUserAPI/doUserFeedBackById";
	/**
	 * 获取验证码
	 */
	public final static String Login_Yzm = GLOBAL_URL
			+ "/VegetableUserAPI/getVerifyCodeByUserMobile";
	/**
	 * 登录接口
	 */
	public final static String login = GLOBAL_URL
			+ "/VegetableUserAPI/vegetable_user_login";
	/**
	 * 版本更新
	 */
	public final static String versionUndate = GLOBAL_URL
			+ "/VegetableUserAPI/getCheckUpdateInfo";
	/**
	 * 微信支付
	 */
	public static final String DOWXPAYBYID = GLOBAL_URL
			+ "/VegetablePayAPI/weixin/";
	/**
	 * 支付失败接口
	 */
	public static final String PAYFAIL = GLOBAL_URL
			+ "/Home/VegetablePayAPI/cancelOrder";
	/**
	 * 更新检查接口
	 */
	public static final String UPDATE = GLOBAL_URL
			+ "/Home/VegetableUserAPI/getCheckUpdateInfo";
	// APP_ID 替换为你的应用从官方网站申请到的合法appId
	public static final String APP_ID = "wx6e1d9f7980a2aea8";

	public static final String APP_NAME = "精菜";
}
