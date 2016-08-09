package com.example.administrator.myapplication.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.activity.OrderListActivity;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.model.FirstEvent;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import de.greenrobot.event.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, Contants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			finish();
			if (resp.errCode == 0) {
				Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
				Intent intent1 = new Intent(WXPayEntryActivity.this,
						OrderListActivity.class);
				startActivity(intent1);
				// 刷新订单页面
				EventBus.getDefault().post(new FirstEvent(0));
			} else if (resp.errCode == -1) {
				Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
			} else if (resp.errCode == -2) {
				Toast.makeText(this, "取消支付", Toast.LENGTH_SHORT).show();
			}
			// AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle(R.string.app_tip);
			// builder.setMessage(getString(R.string.pay_result_callback_msg,
			// String.valueOf(resp.errCode)));
			// builder.show();
		}
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}