package com.example.administrator.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.EvenBusBean;
import com.example.administrator.myapplication.model.LoginInfoBean;
import com.example.administrator.myapplication.model.YzmBean;
import com.example.administrator.myapplication.utils.NetWorkUtil;
import com.example.administrator.myapplication.utils.UserUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity implements OnClickListener {

	protected String newyzm;
	private TimeCount time;
	private TextView tv_resend;
	private EditText et_phone;
	private EditText et_yzm;
	private EditText et_yq;
	private String status;
	private Button bt_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("登录");
		setContentView(R.layout.login);
		bt_login = (Button) findViewById(R.id.bt_login);
		et_yq = (EditText) findViewById(R.id.et_yq);
		tv_resend = (TextView) findViewById(R.id.tv_resend); // 发送按钮
		et_phone = (EditText) findViewById(R.id.et_phone); // 电话号码
		et_yzm = (EditText) findViewById(R.id.et_yzm); // 验证码
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
		bt_login.setOnClickListener(this);
		tv_resend.setOnClickListener(this); // 发送验证码
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_login:
			String phone = et_phone.getText().toString();
			String yqm = et_yq.getText().toString();
			String yzm = et_yzm.getText().toString().trim();
			if (!UserUtil.isMobileNO(phone)) {
				showShortToastMessage("手机号码格式不正确");
				return;
			}
			if (yzm.equals("")) {
				Toast.makeText(LoginActivity.this, "请输入验证码", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (!yzm.equals(newyzm)) {
				Toast.makeText(LoginActivity.this, "请输入正确的验证码",
						Toast.LENGTH_SHORT).show();
				return;
			}
			subyzm(yzm, phone, yqm);
			break;
		case R.id.tv_resend:
			String textphone = et_phone.getText().toString();
			if (UserUtil.isMobileNO(textphone)) {
				sendyzm(textphone);
			} else {
				showShortToastMessage("手机号码格式不正确");
			}
			break;

		default:
			break;
		}
	}

	// 倒计时
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			tv_resend.setText("重新发送");
			tv_resend.setTextColor(Color.argb(255, 17, 17, 17));
			tv_resend.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			tv_resend.setClickable(false);
			tv_resend.setTextColor(Color.argb(255, 187, 187, 187));
			tv_resend.setText(millisUntilFinished / 1000 + "秒后重发");
		}
	}

	// 重发按钮 重新获取验证码
	public void sendyzm(final String mobile) {
		time.start();// 开始计时
		new Thread(new Runnable() {
			JSONObject jsonObject;
			String code;

			@Override
			public void run() {
				String Json = NetWorkUtil.GetDate(Contants.Login_Yzm
						+ "/mobile/" + mobile);
				Log.e("验证码", "获取验证码：" + Json);
				Gson gson = new Gson();
				final YzmBean YzmBean = gson.fromJson(Json, YzmBean.class);
				if (!YzmBean.code.equals("0")) {
					code = YzmBean.code;
					newyzm = YzmBean.data.verifycode;
				}

				// 这儿是耗时操作，完成之后更新UI；
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 更新UI
						if ("1".equals(code)) {
							// 处理邀请码选择栏
							status = YzmBean.data.status; // 手机号是否存在(1代表已存在,0代表不存在)
							if ("1".equals(status)) {
								View ll_yq = findViewById(R.id.ll_yq);
								View v_yq = findViewById(R.id.v_yq);
								et_yq.setText("");
								ll_yq.setVisibility(View.GONE);
								v_yq.setVisibility(View.GONE);
							}

							Toast.makeText(LoginActivity.this, "获取成功",
									Toast.LENGTH_SHORT).show();
							// et_yzm.setText(yzm);
							// /***************************************************开发中采用短信
							// 这里要给去了 只是方便测试******************/
						} else {

							Toast.makeText(LoginActivity.this,
									YzmBean.msg + "", Toast.LENGTH_SHORT)
									.show();
						}
					}

				});
			}
		}).start();
	}

	//
	// 提交-证码 登录
	public void subyzm(final String yzm, final String phone,
			final String invite_code) {
		bt_login.setText("登录中...");
		new Thread(new Runnable() {
			JSONObject jsonObject;
			String code;

			@Override
			public void run() {
				final String Json = NetWorkUtil.GetDate(Contants.login
						+ "/mobile/" + phone + "/verify_code/" + yzm
						+ "/invite_code/" + invite_code);
				Log.d("登录返回：", "" + Json);

				// 这儿是耗时操作，完成之后更新UI；
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Gson gson = new Gson();
						LoginInfoBean fromJson = gson.fromJson(Json,
								LoginInfoBean.class);
						code = fromJson.code;
						// 更新UI
						if ("1".equals(code)) {
							String userId = fromJson.data.id; // 用户id
							SharedPreferences userconfig = LoginActivity.this
									.getSharedPreferences("userconfig", 0);
							Editor edit = userconfig.edit();
							edit.putString("userId", userId).commit(); // 保存用户ID
							edit.putString("nickname", fromJson.data.nickname)
									.commit();
							edit.putString("mobile", fromJson.data.mobile)
									.commit();

							// 发送 登录成功后 让首页重新显示用户信息
							EventBus.getDefault().post(
									new EvenBusBean("reviseNickname"));
							Intent intent = new Intent(LoginActivity.this,
									MainActivity.class);
							LoginActivity.this.startActivity(intent);
							finish();
							Toast.makeText(LoginActivity.this, "登录成功",
									Toast.LENGTH_SHORT).show();
							bt_login.setText("登录成功");

							return;
						} else {
							// 登录失败
							Toast.makeText(LoginActivity.this, fromJson.msg,
									Toast.LENGTH_SHORT).show();
							bt_login.setText("登录");
						}

						bt_login.setText("登录");
					}

				});

			}
		}).start();
	}

}
