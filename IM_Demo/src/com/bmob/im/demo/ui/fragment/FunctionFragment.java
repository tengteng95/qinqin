package com.bmob.im.demo.ui.fragment;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.MessageRecentAdapter;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.Fun_DrawboardActivity;
import com.bmob.im.demo.ui.Fun_LocationActivity;
import com.bmob.im.demo.ui.Fun_RemindActivity;
import com.bmob.im.demo.ui.Fun_SharemoodActivity;
import com.bmob.im.demo.ui.LoginActivity;
import com.bmob.im.demo.ui.SetMyInfoActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.bmob.im.db.BmobDB;

public class FunctionFragment extends FragmentBase implements OnClickListener{

	TextView fun_drawboard,fun_location,fun_sharemood,fun_remind;
	int flag_drawboard_unread=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_function, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	void initView()
	{
		fun_drawboard=(TextView)findViewById(R.id.fun_drawboard);
		fun_location=(TextView)findViewById(R.id.fun_location);
		fun_sharemood=(TextView)findViewById(R.id.fun_sharemood);
		fun_remind=(TextView)findViewById(R.id.fun_remind);
		
		fun_drawboard.setOnClickListener(this);
		fun_location.setOnClickListener(this);
		fun_sharemood.setOnClickListener(this);
		fun_remind.setOnClickListener(this);
	}
	
	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					fun_drawboard.setTextColor(Color.GREEN);
					flag_drawboard_unread=1;//设置为未读
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fun_drawboard:// 启动到涂鸦墙页面
			if(flag_drawboard_unread==1)//存在未读消息
			{
				flag_drawboard_unread=0;//更改为已读
				fun_drawboard.setTextColor(Color.BLACK);
				if(flag_drawboard_unread==0)//如果所有消息都是已读状态
				{
					//iv_fun_tips.setVisible(gone);
				}
			}
			startAnimActivity(new Intent(getActivity(),Fun_DrawboardActivity.class));
			break;
		case R.id.fun_location:// 启动到亲密定位页面
			startAnimActivity(new Intent(getActivity(),Fun_LocationActivity.class));
			break;
		case R.id.fun_sharemood://启动到心情共振页面
			startAnimActivity(new Intent(getActivity(),Fun_SharemoodActivity.class));
			break;
		case R.id.fun_remind://启动到便利贴页面
			startAnimActivity(new Intent(getActivity(),Fun_RemindActivity.class));
			break;

		}
	}
}
