package com.example.administrator.myapplication.model;
//用户信息模型
public class UserInfoBean {
	public String code;
	public String msg;
	public UserInfo data;
	
	public class UserInfo{
		public String username; //用户姓名
		public String nickname; //用户昵称
		public String mobile; //电话
		public String companyName; //公司名称
	}

}
