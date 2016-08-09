package com.example.administrator.myapplication.model;

import java.util.List;
//再来一单的模型
public class RepeatOrderBean {
	public String code;
	public String msg;
	public RepeatOrderinfo data;
	
	public class RepeatOrderinfo{
		public String receiver;        //姓名
		public String mobile;      
		public String address;     
		public String ordernum;     
		public String  order_time;     
		public String  coupon_money;   //优惠券金额  
		public String  pay_info;     
		
		public List<RepeatOrder> order_assort;
		
		public class RepeatOrder{
			public String goods_id; //商品id
			public String title; //
			public String number;//代数
			public String sum_money  ; //单个商品的总金额
			public double sum_pounds  ; //总斤数
			
		}

	}
	
	
}
