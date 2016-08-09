package com.example.administrator.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义一个TextView，使其一创建就具有焦点
 * 
 * @author abner
 * 
 */
public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context) {
		super(context);
	}

	/**
	 * 此方法默认返回值为false，在此处将其返回为true就可以使TextView一创建就具有焦点
	 */
	@Override
	public boolean isFocused() {
		return true;
	}

}