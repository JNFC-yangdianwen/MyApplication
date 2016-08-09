package com.example.administrator.myapplication.model;

import java.util.List;
//地址模型
public class AddrsBean {
	public String code;
	public String msg;
	public List<Addr> data;
	
	public class Addr{
		public String id; //id
		public String contact; //姓名
		public String mobile; //电话
		public String detailed_address; //详细地址	
		public String receive_address; //地址	
		public String user_id;  //用户id
		public String add_time;
		public String up_time;
	}

}
