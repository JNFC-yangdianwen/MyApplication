package com.example.administrator.myapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.buyfood.R;

public class WebViewActivity extends Activity {
	private WebView webView;
	public static final String URL = "url";
	//可以修改字体大小

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview_detail);
		
		//接受数据,url就是webview要去加载的链接地址
		String url = getIntent().getStringExtra(URL);
		webView = (WebView) findViewById(R.id.news_detail_wv);
		//webView加载url地址的操作
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
		
	}

		
	}
