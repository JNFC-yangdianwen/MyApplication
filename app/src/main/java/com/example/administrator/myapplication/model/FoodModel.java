package com.example.administrator.myapplication.model;

import java.util.List;

public class FoodModel {
	public String code;
	public String msg;
	public FoodData data;

	public class FoodData {
		public List<Notice> notice_list; // 通知
		public List<Menu1> parent_list; // 菜品

		public class Notice { // 通知类
			public String id; // 通知类容
			public String content; // 通知类容
		}

		public class Menu1 { // 菜品类
			public String id; // 1级标题id
			public String name; // 1级标题名称
			public List<Menu2> child_list; // 2级菜单集合

			public class Menu2 {
				public String id; // 2级标题id
				public String name; // 2级标题id
				public String parent_id; // 1级标题id
				public List<FoodContent> breed_list; // 食物内容集合

				public class FoodContent {

					public String id; // id
					public String content; // 内容描述
					public String title; // 标题
					public String pic_name; // 图片
					public String each_bag_money; // 每袋金额
					public String poundsunit; // 每袋的斤数
					public String each_bag_unit; // 每斤的金额

				}
			}

		}
	}

}
