package com.example.administrator.myapplication.model;

import java.util.List;
//地址模型
public class CouponBean {
	public String code;
	public String msg;
	public List<Coupon> data;
	
	public class Coupon{
		public String coupon_id; //id
		public String usecouponId; //数据库的自增id
		public String couponName; //优惠劵名称
		public String usefultime; //有效期
		public String couponMoney; //金额
		public String status; //使用状态(1代表已使用,0代表未使用)
		public String coupon_expired ; //是否过期
		public String condition;//满多少减
	}

}
