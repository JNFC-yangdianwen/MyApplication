package com.example.administrator.myapplication.view;

import java.lang.reflect.Field;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.NumberPicker;

/**
 * 滑动选择
 * @author zm04
 *
 */
@SuppressLint("NewApi")
public class Nu  extends NumberPicker{

	public Nu(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setDatePickerDividerColor(this);
	}
	 private void setDatePickerDividerColor(NumberPicker datePicker){
	        // Divider changing:
	    
	             
	            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
	            for (Field pf : pickerFields) {
	                if (pf.getName().equals("mSelectionDivider")) {
	                    pf.setAccessible(true);
	                    try {
	                        pf.set(datePicker, new ColorDrawable(Color.parseColor("#33000000")));
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    } 
	                    break;
	                }
	            }
	        }
	    

}
