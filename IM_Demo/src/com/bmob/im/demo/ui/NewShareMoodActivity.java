package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.ShareMood;
import com.bmob.im.demo.util.CollectionUtils;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;

public class NewShareMoodActivity extends ActivityBase implements OnClickListener{
	LinearLayout include_gv;//包含gridView的布局
	GridView gridView;
	EditText et_content;//心情内容
	Button bn_send;//发送按钮
	RelativeLayout rlayout_whosee;//选择那些亲人可见的布局，用于设置点击事件
	TextView tv_whosee;//用于更新当前心情那些人可见的模式（所有人可见、选中亲人可见）

	BmobChatUser current_user;//当前用户
	BmobRelation to;//可以看到此条心情的用户
	List<BmobChatUser> list;//好友列表
	List<BmobChatUser> checkedUser;//被选中的好友列表
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fun_sharemood_new);
		init();
		bn_send.setOnClickListener(this);
		rlayout_whosee.setOnClickListener(this);
	}

	void init() {
		//获取当前用户
		current_user=BmobUserManager.getInstance(this).getCurrentUser();
		//获取当前好友列表
		list=CollectionUtils.map2list(CustomApplcation.getInstance().getContactList());
		//实例化选中好友列表
		checkedUser = new ArrayList<BmobChatUser>();
		
		//布局中的组件
		include_gv =(LinearLayout)findViewById(R.id.include_choose_friend_layout);
		gridView = (GridView) findViewById(R.id.choose_friend_gridview);
		et_content = (EditText) findViewById(R.id.fun_sharemood_new_content);
		bn_send = (Button)findViewById(R.id.fun_sharemood_new_send);
		rlayout_whosee = (RelativeLayout) findViewById(R.id.fun_sharemood_rlayout_whosee);
		tv_whosee = (TextView)findViewById(R.id.fun_sharemood_tv_whosee);
	
		
		//为gridView添加Adapter
		CustomApplcation ca = CustomApplcation.getInstance();
		HashMap<String, BmobChatUser> map = (HashMap<String, BmobChatUser>) ca.getContactList();
		list=CollectionUtils.map2list(map);
		//_GridViewAdapter adapter = new _GridViewAdapter(this, list);
		//gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				BmobChatUser user=list.get(position);
				String user_all="";
				if(checkedUser.contains(user))
				{//如果已经被选中，取消选中，去除选中标记
					checkedUser.remove(user);
					view.findViewById(R.id.choose_friend_item_iv_tips).setVisibility(View.GONE);
					
					for(BmobChatUser u:checkedUser)
					{
						user_all+=(u.getUsername()+" ");
					}
					ShowToast(user_all);
				}
				else{//尚未被选中
					checkedUser.add(user);
					view.findViewById(R.id.choose_friend_item_iv_tips).setVisibility(View.VISIBLE);

					for(BmobChatUser u:checkedUser)
					{
						user_all+=(u.getUsername()+" ");
					}
					ShowToast(user_all);
				}

			}

		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id=v.getId();
		switch(id)
		{
		case R.id.fun_sharemood_new_send:
			String content=et_content.getText().toString();
			
			ShareMood sm=new ShareMood();
			sm.setUserFrom(current_user);
			to= new BmobRelation();
			if(checkedUser.size()>0)
			{
				//挑选了可见亲人
				for(BmobChatUser user:checkedUser)
				{
					to.add(user);
				}
			}
			else{
				//没有挑选可见亲人，默认为所有亲人可见
				for(BmobChatUser user:list)
				{
					to.add(user);
				}
			}
			to.add(current_user);
			sm.setUserTo(to);
			sm.setContent(content);
			
			sm.save(this, new SaveListener(){

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ShowToast("啊！心情发送失败了!");
				}

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					ShowToast("心情发送成功！");
				}
				
			});
			this.finish();
			break;
		case R.id.fun_sharemood_rlayout_whosee:
			if(include_gv.getVisibility()==View.VISIBLE)
			{
				//如果当前可见，点击后，将更改为当前不可见，同时更新模式文字
				include_gv.setVisibility(View.GONE);
				tv_whosee.setText("所有亲人可见");
			}
			else if(include_gv.getVisibility()==View.GONE)
			{
				include_gv.setVisibility(View.VISIBLE);
				tv_whosee.setText("仅选中亲人可见");
			}
			break;
		}
		
	}
}
