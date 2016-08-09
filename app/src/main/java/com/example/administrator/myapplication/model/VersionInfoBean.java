package com.example.administrator.myapplication.model;


public class VersionInfoBean {
	public String code;
	public String msg;
	public VersionInfo data;
	
	public class VersionInfo{
		public String id; //id
		public String version_code;
		public String version_name; 
		public String url; 
	}
}
