package com.example.administrator.myapplication.model;

public class OrderVo {
		public String order_id; //id
		public String ordernum; //订单号
		public String title_arr;//标题
		public String money; //金额
		public int goods_num; //数量
		public String order_time; // 订单时间
		public String status;  //支付状态(0代表订单取消,1代表商家已接单,2代表已支付已确认收货)
}
