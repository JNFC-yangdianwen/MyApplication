package com.example.administrator.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.InviteCodeBean;
import com.example.administrator.myapplication.utils.UserUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sina.weibo.SinaWeibo.ShareParams;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 邀请码页面
 * 
 * @author fengzi
 * 
 */
public class UserInviteCodeActivity extends BaseActivity implements
		OnClickListener {

	private Dialog mDialog;
	private int windowHeight;
	private int windowWidth;
	private TextView tv_code;
	private TextView tv_act;
	private String UserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("我的邀请");
		setContentView(R.layout.activity_invitecode);
		initData();
		share();
		View bt_share = findViewById(R.id.bt_share);
		tv_code = (TextView) findViewById(R.id.tv_code);
		tv_act = (TextView) findViewById(R.id.tv_act);
		bt_share.setOnClickListener(this);
		ShareSDK.initSDK(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_share:
			// 获取用户id
			SharedPreferences userconfig = UserInviteCodeActivity.this
					.getSharedPreferences("userconfig", 0);
			UserId = userconfig.getString("userId", "");
			mDialog.show();
			break;

		default:
			break;
		}
	}

	// 打开分享对话框
	private void share() {
		mDialog = new Dialog(this, R.style.customDialog);
		mDialog.setContentView(R.layout.dialog_layout);
		// mDialog.setCanceledOnTouchOutside(true);
		// WindowManager.LayoutParams params =
		// mDialog.getWindow().getAttributes();
		// params.gravity = Gravity.LEFT | Gravity.BOTTOM;//这个设置使这个dialog从上方弹出来
		// params.windowAnimations = 1;
		// 取消对话框
		View tv_cancel = mDialog.findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		//
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		windowHeight = display.getHeight();
		windowWidth = display.getWidth();
		// params.width = windowWidth;
		// params.height = windowHeight;

		Window dialogWindow = mDialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = windowWidth;
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		mDialog.findViewById(R.id.llWxpyq).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
						Bitmap bmp = BitmapFactory.decodeResource(
								getResources(), R.drawable.ic_launcher);
						sp.setImageData(bmp);
						sp.setShareType(Platform.SHARE_WEBPAGE);
						sp.setTitle("分享我的邀请码");
						sp.setUrl("http://t13.beauityworld.com/Home/HouseUserAPI/yqm?userID="
								+ UserId);
						sp.setText("精菜欢迎你");
						Platform wechatMoments = ShareSDK
								.getPlatform(WechatMoments.NAME);
						wechatMoments.share(sp);
					}
				});
		mDialog.findViewById(R.id.llWxhy).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Wechat.ShareParams sp = new Wechat.ShareParams();
						sp.setShareType(Platform.SHARE_WEBPAGE);
						sp.setTitle("分享我的邀请码");
						sp.setUrl("http://t13.beauityworld.com/Home/HouseUserAPI/yqm?userID="
								+ UserId);
						sp.setText("精菜欢迎你");
						Platform weChat = ShareSDK.getPlatform(Wechat.NAME);
						weChat.share(sp);
					}
				});
		mDialog.findViewById(R.id.llsina).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						ShareParams sp = new ShareParams();
						sp.setText("分享我的邀请码");
						Bitmap bmp = BitmapFactory.decodeResource(
								getResources(), R.drawable.ic_launcher);
						sp.setImageData(bmp);
						sp.setUrl("http://t13.beauityworld.com/Home/HouseUserAPI/yqm?userID="
								+ UserId);
						Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
						// weibo.setPlatformActionListener(); // 设置分享事件回调
						// 执行图文分享
						weibo.share(sp);
					}
				});

	}

	/**
	 * 网络请求
	 */
	public void initData() {

		waitDialog.show();
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.getCode;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("user_id", usrId);
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				waitDialog.dismiss();
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					Log.e("userinfoactivity", result + "");
					Gson gson = new Gson();
					InviteCodeBean inviteCodeBean = gson.fromJson(result,
							InviteCodeBean.class);
					if ("1".equals(inviteCodeBean.code)) {
						tv_code.setText(inviteCodeBean.data.user_code);
						tv_act.setText(inviteCodeBean.data.acvity_content);

						// 完成操作
					} else {
						showShortToastMessage(inviteCodeBean.msg + "");
					}

				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				showShortToastMessage("网络加载异常，请稍后再试");
			}

		});
	}

}