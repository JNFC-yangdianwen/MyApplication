package com.example.administrator.myapplication.model;
//最低消费金额
public class InviteCodeBean {
	public String code;
	public String msg;
	public InviteCode data;
	
	public class InviteCode{
		public String user_code; //邀请码
		public String acvity_content ; //活动
	}

}
