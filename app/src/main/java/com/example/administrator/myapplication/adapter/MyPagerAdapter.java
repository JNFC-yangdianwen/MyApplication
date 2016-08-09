package com.example.administrator.myapplication.adapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
public class MyPagerAdapter extends PagerAdapter
{
	private List<TextView> list;
	/**@param list ImageView的集合*/
	public MyPagerAdapter(List<TextView> list){
		this.list = list;
	}
	@Override
	public int getCount(){
		return (list==null)?0:list.size();
	}
	/**类似于 baseAdapter的 getView 用于，产生一个View
	 * @param container 是指ViewPager
	 * @param position 当前页面在ViewPager中的位置
	 * @return 集合中的图片*/
	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		View child = list.get(position);
		if(child.getParent()!=null){
			((ViewPager)container).removeView(child);
		}
		container.addView(list.get(position));/**将List 集合的ImageView添加到 ViewPager内*/
		return list.get(position);
	}
	/**新来数据，新建页面
	 * @param view 标准View对象
	 * @param object instantiateItem 的返回值
	 * @return 如果object是View的对象，那么为true*/
	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view==object;
	}
	/**从ViewPager中移除一个指定的View<br/>
	 * super.destroyItem(container, position, object); 一定要删掉*/
	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		//		container.removeView(list.get(position%list.size()));
	}
}
