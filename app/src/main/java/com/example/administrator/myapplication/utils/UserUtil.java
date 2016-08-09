package com.example.administrator.myapplication.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;

public class UserUtil {
	public static String getUsrId(Context context){
		SharedPreferences userconfig  = context.getSharedPreferences("userconfig", 0);
		String userId = userconfig.getString("userId", "no");
		if(!"no".equals(userId)){
			return userId;
		}
		return "no";
	}
	
	
	
	public  static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
		}
	
	/**
	 * 判断是否数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)
	{
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
			if( !isNum.matches() )
			{
				return false;
			}
		return true;
	} 
 
}
