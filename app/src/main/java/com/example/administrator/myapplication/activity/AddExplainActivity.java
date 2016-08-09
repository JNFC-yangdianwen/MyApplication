package com.example.administrator.myapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.buyfood.R;
import com.example.administrator.myapplication.model.EvenBusBean;

import de.greenrobot.event.EventBus;
/**
 * 添加备注
 * @author fengzi
 *
 */
public class AddExplainActivity extends BaseActivity implements OnClickListener{

	private EditText editText1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isTemplate = true;
		super.onCreate(savedInstanceState);
		titleView.setText("添加备注");
		templateTextViewRight.setVisibility(View.VISIBLE);
		templateTextViewRight.setText("确定");
		setContentView(R.layout.activity_sugges);
		 editText1 = (EditText) findViewById(R.id.et_text);
		 templateTextViewRight.setOnClickListener(this);
		 String bztext = getIntent().getStringExtra("bztext");
		 editText1.setText(bztext+"");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_tv_right:
			String text = editText1.getText().toString();
//    		发送  添加备注后 将备注内容发送到订单页面
    	    EventBus.getDefault().post(new EvenBusBean("beizhu",text)); 
			finish();
			break;

		default:
			break;
		}
	}
	
	
	
}