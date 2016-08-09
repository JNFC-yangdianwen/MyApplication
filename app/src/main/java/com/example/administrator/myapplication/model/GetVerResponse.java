package com.example.administrator.myapplication.model;

/**
 * 
 * 获取版本
 * 
 * @author gc
 * 
 */
public class GetVerResponse {

	public String code;// 用户编号
	public String msg;// 用户名
	public response data;

	public static class response {
		public String url; // 是否更新客户端缓存的数据； true ： 更新；false：不更新
		public int id;
		public double version_code;
		public String add_time;
	}
}
