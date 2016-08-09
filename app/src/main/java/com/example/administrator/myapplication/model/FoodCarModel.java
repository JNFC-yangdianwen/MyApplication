package com.example.administrator.myapplication.model;

import java.math.BigDecimal;

//购物车模型
public class FoodCarModel {
	public String id;
	public String title;
	public int daiSumNum; // 有几袋的数量
	public double pounds; // 一袋几斤
	public double each_bag_money; // 每袋单价（总价=袋*袋单价）

	// 保留2位小数处理工具
	public static String convertDouble(double number) {
//		BigDecimal b = new BigDecimal(number);
//		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if (number == 0) {
			return "0.00";
		}
		return Long2Double(number);
	}
	/**
	 * 
	 * @author czz
	 * @createdate 2015-9-18 下午3:23:01
	 * @Description: (将金额转换的 如：100.00的格式)
	 * @param money
	 * @return
	 *
	 */
	public static String Long2Double(Double money){
		java.text.DecimalFormat df=new java.text.DecimalFormat("#.00");
		return df.format(money);
	}
}
