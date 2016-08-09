package com.example.administrator.myapplication.model;
//最低消费金额
public class LowestBean {
	public String code;
	public String msg;
	public Lowest data;
	
	public class Lowest{
		public String id; //id
		public String money; //最低金额
	}

}
