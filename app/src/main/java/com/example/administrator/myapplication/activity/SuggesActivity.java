package com.example.administrator.myapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.buyfood.R;
import com.example.administrator.myapplication.globle.Contants;
import com.example.administrator.myapplication.utils.UserUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 意见反馈
 * 
 * @author fengzi
 * 
 */
public class SuggesActivity extends BaseActivity implements OnClickListener {

	private EditText et_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("意见反馈");
		templateTextViewRight.setVisibility(View.VISIBLE);
		templateTextViewRight.setText("提交");
		setContentView(R.layout.activity_sugges);
		et_text = (EditText) findViewById(R.id.et_text);
		templateTextViewRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		String text = et_text.getText().toString();
		if (text != null && !"".equals(text)) {
			subText(text);
		} else {
			Toast.makeText(SuggesActivity.this, "说点内容在提交哟", Toast.LENGTH_SHORT)
					.show();
		}

	}

	public void subText(String text) {
		String usrId = UserUtil.getUsrId(this);
		HttpUtils http = new HttpUtils();
		String url = Contants.suggest;
		RequestParams params = new RequestParams();
		// 添加请求参数
		params.addBodyParameter("user_id", usrId);
		params.addBodyParameter("content", text);
		waitDialog.show();
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
			private String code;

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				int statusCode = responseInfo.statusCode;
				if (statusCode == 200) {
					String result = responseInfo.result;
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result);
						code = jsonObject.get("code").toString();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if ("1".equals(code)) {
						Toast.makeText(SuggesActivity.this,
								"O(∩_∩)O 谢谢您的宝贵意见，我们会努力做得更好！",
								Toast.LENGTH_SHORT).show();
						SuggesActivity.this.finish();

					} else {
						Toast.makeText(SuggesActivity.this, "提交失败",
								Toast.LENGTH_SHORT).show();
					}
					waitDialog.dismiss();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				waitDialog.dismiss();
				Toast.makeText(SuggesActivity.this, "网络加载异常，请稍后再试",
						Toast.LENGTH_SHORT).show();
			}

		});
	}

}