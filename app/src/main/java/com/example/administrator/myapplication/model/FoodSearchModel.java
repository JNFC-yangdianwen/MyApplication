package com.example.administrator.myapplication.model;

import java.util.List;

public class FoodSearchModel {
	public String code;
	public String msg;
	public List <FoodSearch> data;
	
	
	public class FoodSearch{
		public String id;    //id
		public String content;    //内容描述
		public String title;    //标题
		public String pic_name;    //图片
		public String each_bag_money;    //每袋金额
		public String pounds ;    //每袋的斤数
		public String pounds_money ;    //每斤的金额
		public String poundsunit ;    //每袋的斤数
		public String each_bag_unit ;    //每斤的金额
	}
	
	
}
