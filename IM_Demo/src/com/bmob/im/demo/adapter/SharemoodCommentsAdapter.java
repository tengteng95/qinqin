package com.bmob.im.demo.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.ShareMood;
import com.bmob.im.demo.bean.ShareMood_Comments;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**心情共享评论列表
  * @ClassName: SharemoodCommentsAdapter
  * @Description: 发表动态时用于选择可以看到此条动态的亲人
  * @author Mr.Huang
  * @date 2015-7-28 
  */
public class SharemoodCommentsAdapter extends BaseAdapter  {
	private Context ct;
	private List<ShareMood_Comments> list_comment;
	private ShareMood sharemood;//当前评论列表适配器所属的动态

	public SharemoodCommentsAdapter(Context ct, List<ShareMood_Comments> list) {
		this.ct = ct;
		this.list_comment = list;
	}

	/** 当ListView数据发生变化时,调用此方法来更新ListView
	  * @Title: updateListView
	  * @Description: TODO
	  * @param @param list 
	  * @return void
	  * @throws
	  */
	public void updateListView(List<ShareMood_Comments> list) {
		this.list_comment = list;
		notifyDataSetChanged();
	}

	public void remove(ShareMood_Comments comment){
		this.list_comment.remove(comment);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list_comment.size();
	}

	@Override
	public Object getItem(int position) {
		return list_comment.get(position);
	}

	@Override
	public long getItemId(int position) {
		//return 0;
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ct).inflate(
					R.layout.item_sharemood_comment, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.item_sharemood_comment_username);
			viewHolder.comment = (TextView) convertView
					.findViewById(R.id.item_sharemood_comment_comment);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ShareMood_Comments cm = list_comment.get(position);
		final String str_name = cm.getUser().getUsername();
		final String str_nick = cm.getUser().getNick();
		final String str_comment = cm.getContent();

		if (!TextUtils.isEmpty(str_nick)) {
			viewHolder.name.setText(str_nick);
		} else {
			viewHolder.name.setText(str_name);
		}
		viewHolder.comment.setText(str_comment);
		return convertView;
	}

	static class ViewHolder {
		TextView name;
		TextView comment;
	}
	
	public void setBelongShareMood(ShareMood sharemood)
	{
		this.sharemood=sharemood;
	}
	
	public ShareMood getBelongShareMood(ShareMood sharemood)
	{
		return sharemood;
	}
	


}
