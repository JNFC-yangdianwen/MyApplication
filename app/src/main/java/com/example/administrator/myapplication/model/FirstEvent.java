package com.example.administrator.myapplication.model;

public class FirstEvent {

	private String mMsg;
	private int hour;
	private int type;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public FirstEvent(int type) {
		super();
		this.type = type;
	}
	public FirstEvent(String msg) {
		// TODO Auto-generated constructor stub
		mMsg = msg;
	}
	public FirstEvent(String msg,int hour) {
		// TODO Auto-generated constructor stub
		mMsg = msg;
		this.hour =hour;
	}
	public String getMsg(){
		return mMsg;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	
}
