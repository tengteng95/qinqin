package com.bmob.im.demo.ui;

import java.util.List;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.Note;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class Fun_RemindActivity extends ActivityBase {

	GridView gridView;
	TextView tv_new;
	BmobChatUser current_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fun_remind);

		current_user = BmobUserManager.getInstance(this).getCurrentUser();
		init();// 初始化组件
		getMyRemind();// 获取我的提示
	}

	void init() {
		gridView = (GridView) findViewById(R.id.activity_fun_remind_gridview);
		tv_new = (TextView) findViewById(R.id.activity_fun_remind_new);
	}

	void getMyRemind() {
		BmobQuery<Note> query = new BmobQuery<Note>();
		query.addWhereEqualTo("to", current_user);
		query.setLimit(10);
		query.order("-createdAt");
		query.include("from");
		query.findObjects(this, new FindListener<Note>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("error");

			}

			@Override
			public void onSuccess(List<Note> arg0) {
				// TODO Auto-generated method stub

				ShowToast("success");
				
			}

		});

	}
}
