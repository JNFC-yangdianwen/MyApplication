package com.example.administrator.myapplication.model;

import java.util.List;
//地址模型
public class SendTimeBean {
	public String code;
	public String msg;
	public List<SendTime> data;
	
	public class SendTime{
		public String today_week; //星期
		public String current_date; //日期
		public  List<TimeInfo> duration_list; //时间
		public class TimeInfo{
			public String endtime;
			public String starttime;
		}
	}

}
