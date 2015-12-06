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
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/** 好友列表(网格排列)
  * @ClassName: _GridViewAdapter
  * @Description: 发表动态时用于选择可以看到此条动态的亲人
  * @author Mr.Huang
  * @date 2015-7-28 
  */
@SuppressLint("DefaultLocale")
public class RemindAdapter extends BaseAdapter  {
	private Context ct;
	private List<BmobChatUser> data;

	public RemindAdapter(Context ct, List<BmobChatUser> datas) {
		this.ct = ct;
		this.data = datas;
	}

	/** 当ListView数据发生变化时,调用此方法来更新ListView
	  * @Title: updateListView
	  * @Description: TODO
	  * @param @param list 
	  * @return void
	  * @throws
	  */
	public void updateListView(List<BmobChatUser> list) {
		this.data = list;
		notifyDataSetChanged();
	}

	public void remove(BmobChatUser user){
		this.data.remove(user);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
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
					R.layout.choose_friend_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.choose_friend_item_name);
			viewHolder.avatar = (ImageView) convertView
					.findViewById(R.id.choose_friend_item_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		BmobChatUser friend = data.get(position);
		final String name = friend.getUsername();
		final String avatar = friend.getAvatar();

		if (!TextUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, viewHolder.avatar, ImageLoadOptions.getOptions());
		} else {
			viewHolder.avatar.setImageDrawable(ct.getResources().getDrawable(R.drawable.head));
		}
		viewHolder.name.setText(name);
		return convertView;
	}

	static class ViewHolder {
		ImageView avatar;
		TextView name;
	}


}
