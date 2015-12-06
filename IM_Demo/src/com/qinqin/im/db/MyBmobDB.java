package com.qinqin.im.db;
import java.util.List;

import com.bmob.im.demo.bean.*;

import android.content.Context;
import android.widget.Toast;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class MyBmobDB //extends BmobDB
{
	static int UnRead_drawboard=0;
	static int UnRead_sharemood=0;

	//不允许在外部实例化
	private MyBmobDB()
	{
		
	}
	
	static int getUnReadDrawBoard(final Context context,BmobChatUser current_user)
	{
		BmobQuery<BmobMsg> query = new BmobQuery<BmobMsg>();
		//首先要是fun_drawboard类型的消息
		query.addWhereEqualTo("tag","fun_drawboard");
		//而且要是发送给当前用户的
		query.addWhereEqualTo("toId",current_user.getObjectId());
		//还要是未读的(0表示未读，1表示已读）
		query.addWhereEqualTo("isReaded", 0);
		query.findObjects(context, new FindListener<BmobMsg>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "查询未读消息出错", Toast.LENGTH_LONG).show();
						UnRead_drawboard=0;
					}

					@Override
					public void onSuccess(List<BmobMsg> arg0) {
						// TODO Auto-generated method stub
						UnRead_drawboard=arg0.size();
					}
			
				});
		return UnRead_drawboard;
		
		
	}
}
