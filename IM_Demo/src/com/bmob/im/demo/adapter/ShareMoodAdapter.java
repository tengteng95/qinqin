package com.bmob.im.demo.adapter;

import java.util.List;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.base.BaseListAdapter;
import com.bmob.im.demo.adapter.base.ViewHolder;
import com.bmob.im.demo.bean.ShareMood;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

public class ShareMoodAdapter extends BaseListAdapter<ShareMood> {

	Handler handler;//用以更新所在Activity布局中的底部评论条
	List<SharemoodCommentsAdapter> list_cmAdapter;//每个item中的listView的Adapter
	public ShareMoodAdapter(Context context, List<ShareMood> list,
			Handler handler,List<SharemoodCommentsAdapter> list_adapter) {
		super(context, list);
		this.handler=handler;
		this.list_cmAdapter=list_adapter;
	}
	

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_sharemood, null);
		}
		
		final ShareMood sharemood = getList().get(position);
		// 使用这个ViewHolder类的用途是不需要在这Adapter类中再定义一个内部ViewHolder类
		// 头像
		ImageView img_avatar = ViewHolder.get(convertView, R.id.item_sharemood_img_avatar);
		// 昵称或用户名
		TextView tv_username = ViewHolder.get(convertView, R.id.item_sharemood_tv_username);
		//发布时间
		TextView tv_date =ViewHolder.get(convertView, R.id.item_sharemood_tv_date);
		// 心情内容
		TextView tv_content = ViewHolder.get(convertView, R.id.item_sharemood_tv_content);
		// 评论
		LinearLayout llayout_comment = ViewHolder.get(convertView, R.id.item_sharemood_llayout_comment);
		// 喜欢
		LinearLayout llayout_like = ViewHolder.get(convertView, R.id.item_sharemood_llayout_like);
		// 显示喜欢这条心情的人
		TextView tv_wholike = ViewHolder.get(convertView, R.id.item_sharemood_tv_wholike);
		//评论栏
		ListView lv_comments= ViewHolder.get(convertView, R.id.item_sharemood_lv_comments);
		
		//为了获知是哪个item中的点赞、评论按钮被点击，需要为每个可点击部分调用setTag的方法存储其item索引值
		llayout_comment.setTag(position);
		llayout_like.setTag(position);

		String avatar = sharemood.getUserFrom().getAvatar();// 获取头像信息
		String nickName = sharemood.getUserFrom().getNick();// 获取昵称
		String userName = sharemood.getUserFrom().getUsername();// 获取用户名，当昵称为空时，显示用户名
		String str_content = sharemood.getContent();// 获取心情内容
		String date= sharemood.getCreatedAt();
		
		/*ShowToast("头像信息:"+avatar);
		ShowToast("昵称信息:"+nickName);
		ShowToast("username："+userName);
		ShowToast("content:"+str_content);*/

		// 设置头像
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, img_avatar, ImageLoadOptions.getOptions());
		} else {
			img_avatar.setImageResource(R.drawable.default_head);
		}

		// 设置昵称
		if (!TextUtils.isEmpty(nickName)) {
			// 昵称不为空
			tv_username.setText(nickName);
		} else if(!TextUtils.isEmpty(userName)){
			tv_username.setText(userName);
		}else{
			tv_username.setText("unknown user!");
		}
		//设置发表时间
		tv_date.setText(date);
		// 设置心情内容
		tv_content.setText(str_content);
		//设置评论,评论列表需要与动态匹配
		SharemoodCommentsAdapter my_smcadapter;
		if(null!=(my_smcadapter=searchCommentAdapter(sharemood)))
		{
			lv_comments.setAdapter(my_smcadapter);
		}else{
			lv_comments.setAdapter(null);
		}
		
		/*if(!TextUtils.isEmpty(comments)){
		tv_comments.setText(comments);}
		else{
			tv_comments.setText("暂时还没有评论哟！");
		}*/
		

		llayout_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowToast("说点什么吧！");
				if(handler!=null)
				{
					Bundle bundle=new Bundle();
					final int position =(Integer) arg0.getTag();//获取索引
					bundle.putSerializable("which_sm", list.get(position));
					Message msg=new Message();
					msg.what=0x1233;
					msg.setData(bundle);
					handler.sendMessage(msg);
					
				}
				else
				{
					ShowToast("异常错误！评论条弹出失败！");
				}
				// TODO Auto-generated method stub
				/*final ProgressDialog progress = new ProgressDialog(mContext);
				progress.setMessage("正在添加...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				// 发送tag请求
				BmobChatManager.getInstance(mContext).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, contract.getObjectId(),
						new PushListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证!");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求失败，请重新添加!");
						ShowLog("发送请求失败:" + arg1);
					}
				});*/
			}
		});

		llayout_like.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowToast("啊！好喜欢！");
				// TODO Auto-generated method stub
				/*final ProgressDialog progress = new ProgressDialog(mContext);
				progress.setMessage("正在添加...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				// 发送tag请求
				BmobChatManager.getInstance(mContext).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, contract.getObjectId(),
						new PushListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证!");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求失败，请重新添加!");
						ShowLog("发送请求失败:" + arg1);
					}
				});*/
			}
		});
		return convertView;
	}
	
	//寻找是否有与当前动态匹配的评论列表
	SharemoodCommentsAdapter searchCommentAdapter(ShareMood sm)
	{
		for(SharemoodCommentsAdapter smc_adapter:list_cmAdapter)
		{
			if(sm==smc_adapter.getBelongShareMood(sm))
				return smc_adapter;
		}
		return null;
		
	}

}
