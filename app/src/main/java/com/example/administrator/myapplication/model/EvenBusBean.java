package com.example.administrator.myapplication.model;

public class EvenBusBean {
	public String msg;
	public String msg2;
	public String msg3;
	public String msg4;

	public EvenBusBean(String msg) {
		// TODO Auto-generated constructor stub
		this.msg = msg;
	}

	public EvenBusBean(String msg, String msg2) {
		// TODO Auto-generated constructor stub
		this.msg = msg;
		this.msg2 = msg2;
	}

	public EvenBusBean(String msg, String msg2, String msg3, String msg4) {
		// TODO Auto-generated constructor stub
		this.msg = msg;
		this.msg2 = msg2;
		this.msg3 = msg3;
		this.msg4 = msg4;
	}

	public String getMsg4() {
		return msg4;
	}

	public void setMsg4(String msg4) {
		this.msg4 = msg4;
	}

	public String getMsg() {
		return msg;
	}

	public String getMsg2() {
		return msg2;
	}

	public String getMsg3() {
		return msg3;
	}
}
