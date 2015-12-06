package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.DrawBoard;
import com.bmob.im.demo.config.myBmobConfig;
import com.bmob.im.demo.util.CollectionUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;

public class Fun_DrawboardActivity extends ActivityBase implements OnClickListener{
	TextView content;
	Button button;
	BmobChatUser cuser;
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				if (cuser != null) {
					content.setText("新公告！" + cuser.getUsername());
				}
			} else if (msg.what == 0x1233) {
				Bundle bundle = msg.getData();
				content.setText((String) bundle.getSerializable("content"));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fun_drawboard);
		init();
	}

	void init() {
		content = (TextView) findViewById(R.id.fun_drawboard_content);
		button = (Button) findViewById(R.id.fun_drawboard_add);
		button.setOnClickListener(this);
		cuser = BmobUserManager.getInstance(this).getCurrentUser();
		/*更新公告栏内容时，显示的最新的公告，这个公告要么是当前用户发送的，要么是当前用户需要接收的，所以要用到
		 * 复合“或”查询，返回的公告结果包括用户发送的公告或者用户需要接受的公告
		 */
		BmobQuery<DrawBoard> query1 = new BmobQuery<DrawBoard>();
		query1.addWhereEqualTo("to", cuser);
		BmobQuery<DrawBoard> query2 = new BmobQuery<DrawBoard>();
		query2.addWhereEqualTo("from", cuser);
		List<BmobQuery<DrawBoard>> queries = new ArrayList<BmobQuery<DrawBoard>>();
		queries.add(query1);
		queries.add(query2);
		BmobQuery<DrawBoard> mainQuery = new BmobQuery<DrawBoard>();
		mainQuery.or(queries);
		mainQuery.order("-updatedAt");
		mainQuery.findObjects(this, new FindListener<DrawBoard>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("异常！更新公告栏出错！"+arg1);
			}

			@Override
			public void onSuccess(List<DrawBoard> arg0) {
				// TODO Auto-generated method stub

				Message msg = new Message();
				msg.what = 0x1233;
				Bundle bundle = new Bundle();
				if (arg0.size() > 0) {
					bundle.putSerializable("content", ((DrawBoard) (arg0.get(0))).getContent());
					msg.setData(bundle);
					handler.sendMessage(msg);
					ShowToast("更新公告栏成功！");
				}
				else
				{
					ShowToast("更新公告栏成功！暂无新公告！");
				}
			}

		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,NewDrawBoardActivity.class);
		startActivityForResult(intent,0);
	}
	
	@Override
	public void onActivityResult(int requestcode,int resultcode,Intent intent)
	{
		if(requestcode==0&resultcode==0)
		{
			Bundle data=intent.getExtras();
			try{
			    String str=data.getString("content");
			    content.setText(str);
			}
			catch(Exception e){}
		}
		
	}

}
