package com.bmob.im.demo.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.EmoViewPagerAdapter;
import com.bmob.im.demo.adapter.EmoteAdapter;
import com.bmob.im.demo.adapter.ShareMoodAdapter;
import com.bmob.im.demo.adapter.SharemoodCommentsAdapter;
import com.bmob.im.demo.bean.FaceText;
import com.bmob.im.demo.bean.ShareMood;
import com.bmob.im.demo.bean.ShareMood_Comments;
import com.bmob.im.demo.ui.ChatActivity.VoiceTouchListen;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.util.FaceTextUtils;
import com.bmob.im.demo.view.EmoticonsEditText;
import com.bmob.im.demo.view.xlist.XListView;
import com.bmob.im.demo.view.xlist.XListView.IXListViewListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class Fun_SharemoodActivity extends ActivityBase implements OnClickListener, IXListViewListener {

	XListView xlist_content;
	Button btn_new;
	ShareMood current_sharemood;// 当前被点击评论按钮或者点赞按钮的心情动态
	BmobChatUser current_user;
	ShareMoodAdapter adapter;
	int currentPager = 0;// 当前页数
	int num_eachPage = 10;
	List<ShareMood> list = new ArrayList<ShareMood>();// 用户可以看到的心情共享列表
	// 心情列表每个item中的listView的adapter
	List<SharemoodCommentsAdapter> list_cmAdapter = new ArrayList<SharemoodCommentsAdapter>();
	// 暂时存放每条心情的评论列表，在构建数据适配adapter中起到暂存的作用
	List<ShareMood_Comments> list_smc = new ArrayList<ShareMood_Comments>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fun_sharemood);
		init();
		getShareMood();// 获取亲人发布的心情
	}

	void init() {

		// edit_user_comment =
		// (EmoticonsEditText)findViewById(R.id.edit_user_comment);
		xlist_content = (XListView) findViewById(R.id.sharemood_xlist_content);
		// 允许加载更多
		xlist_content.setPullLoadEnable(true);
		// 允许下拉
		xlist_content.setPullRefreshEnable(true);
		// 设置监听器
		xlist_content.setXListViewListener(this);
		xlist_content.pullRefreshing();
		xlist_content.setDividerHeight(0);
		btn_new = (Button) findViewById(R.id.sharemood_btn_new);
		btn_new.setOnClickListener(this);

		current_user = BmobUserManager.getInstance(this).getCurrentUser();
	}

	void getShareMood() {
		BmobQuery<ShareMood> query = new BmobQuery<ShareMood>();
		query.addWhereEqualTo("to", current_user);
		query.order("-createdAt");
		query.setLimit(num_eachPage);// 分页查询，
		query.include("from");// 同时要把发布人的信息也查找出来，必须要添加此句，否则调用发布人所属类的get方法时得到的将为null
		//query.setCachePolicy(CachePolicy.CACHE_ONLY);// 默认进入时只从缓存中获取，避免用户等待的情况
		query.findObjects(this, new FindListener<ShareMood>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("获取心情动态失败！");

			}

			@Override
			public void onSuccess(List<ShareMood> arg0) {
				// TODO Auto-generated method stub
				// 传handler是为了方便在listView中的组件添加点击事件，更新当前Activity的底部评论条
				for (ShareMood sm : arg0) {
					list.add(sm);
					findComments(sm);// 查找评论，组成adapter并添加到列表
				}
				adapter = new ShareMoodAdapter(Fun_SharemoodActivity.this, arg0, null, list_cmAdapter);
				xlist_content.setAdapter(adapter);
				currentPager++;
			}
		});

		// 暂时显示完毕后，开始从网络获取判断是否有新内容，如果有，获取之
		//getShareMoodFromNetwork(list.get(0).getCreatedAt());// 查找比缓存内容更“新”的心情
	}

	void getShareMoodFromNetwork(String str_date) {
		String pattern = "yyyy-MM-dd HH:mm:ss";// 时间格式
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = sdf.parse(str_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BmobQuery<ShareMood> query = new BmobQuery<ShareMood>();
		query.addWhereEqualTo("to", current_user);
		query.addWhereGreaterThan("createdAt", new BmobDate(date));
		query.order("createdAt");// 从小到大排
		query.setLimit(num_eachPage);// 分页查询，
		query.include("from");// 同时要把发布人的信息也查找出来，必须要添加此句，否则调用发布人所属类的get方法时得到的将为null
		query.setCachePolicy(CachePolicy.NETWORK_ONLY);// 只从网络获取更新的数据（会自动缓存到本地）
		query.findObjects(this, new FindListener<ShareMood>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("获取心情动态失败！");

			}

			@Override
			public void onSuccess(List<ShareMood> arg0) {
				// TODO Auto-generated method stub
				// 传handler是为了方便在listView中的组件添加点击事件，更新当前Activity的底部评论条
				for (ShareMood sm : arg0) {
					list.add(0, sm);
					findComments(sm);// 查找评论，组成adapter并添加到列表
				}
				adapter.setList(list);// 更新数据适配
				currentPager++;
			}
		});
	}

	void findComments(final ShareMood sm) {
		// 心情评论存放列表

		BmobQuery<ShareMood_Comments> query = new BmobQuery<ShareMood_Comments>();
		query.addWhereEqualTo("sharemood", sm);
		query.order("-createdAt");
		query.setLimit(10);// 最多显示10条评论
		query.include("user");// 同时要把评论人的信息也查找出来，必须要添加此句，否则调用评论人所属类的get方法时得到的将为null
		query.findObjects(this, new FindListener<ShareMood_Comments>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("获取评论失败！");

			}

			@Override
			public void onSuccess(List<ShareMood_Comments> arg0) {
				// TODO Auto-generated method stub
				// 传handler是为了方便在listView中的组件添加点击事件，更新当前Activity的底部评论条
				for (ShareMood_Comments smc : arg0) {
					list_smc.add(smc);
				}
				// 构建adapter
				SharemoodCommentsAdapter smc_adapter = new SharemoodCommentsAdapter(Fun_SharemoodActivity.this,
						list_smc);
				smc_adapter.setBelongShareMood(sm);
				// 将adapter添加到列表中
				list_cmAdapter.add(smc_adapter);
			}
		});
		return;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sharemood_btn_new:// 发送新动态
			Intent intent = new Intent(Fun_SharemoodActivity.this, NewShareMoodActivity.class);
			startActivity(intent);
			break;
		// case R.id.btn_chat_send:// 发送文本
		// final String msg = edit_user_comment.getText().toString();
		// if (msg.equals("")) {
		// ShowToast("请输入发送消息!");
		// return;
		// }
		// boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		// if (!isNetConnected) {
		// ShowToast(R.string.network_tips);
		// // return;
		// }
		// // 将评论内容上传到服务器
		// ShareMood_Comments smc = new ShareMood_Comments();
		// smc.setUser(current_user);
		// smc.setContent(msg);
		// smc.setSharemood(current_sharemood);
		// smc.save(Fun_SharemoodActivity.this, new SaveListener() {
		//
		// @Override
		// public void onFailure(int arg0, String arg1) {
		// // TODO Auto-generated method stub
		//
		// ShowToast("异常错误");
		//
		// }
		//
		// @Override
		// public void onSuccess() {
		// // TODO Auto-generated method stub
		// ShowToast("评论发表成功");
		//
		// }
		//
		// });
		// // 刷新界面
		// // refreshMessage(message);
		//
		// break;
		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		ShowToast("尝试刷新");
		BmobQuery<ShareMood> query = new BmobQuery<ShareMood>();
		query.addWhereEqualTo("to", current_user);
		// query.order("-createAt");
		query.setLimit(num_eachPage);// 分页查询
		// query.setSkip(list.size());// 跳过已显示的信息，最好通过时间判断来实现

		query.include("from");// 同时要把发布人的信息也查找出来，必须要添加此句，否则调用发布人所属类的get方法时得到的将为null
		query.findObjects(this, new FindListener<ShareMood>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("获取心情动态失败！");

			}

			@Override
			public void onSuccess(List<ShareMood> arg0) {
				// TODO Auto-generated method stub
				// 传handler是为了方便在listView中的组件添加点击事件，更新当前Activity的底部评论条
				if (arg0.size() <= 0) {
					ShowToast("当前已经没有更多动态");
					return;
				}
				for (ShareMood sm : arg0) {
					list.add(sm);
					findComments(sm);// 查找评论，组成adapter并添加到列表
				}
				adapter.setList(list);// 会自动更新数据源
				currentPager++;// 页数加1
			}
		});
		xlist_content.stopLoadMore();// 不加此句会一直执行加载更多；
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		ShowToast("尝试加载");
		BmobQuery<ShareMood> query = new BmobQuery<ShareMood>();
		query.addWhereEqualTo("to", current_user);
		query.order("-createAt");
		query.setLimit(num_eachPage);// 分页查询
		query.setSkip(currentPager * num_eachPage);// 跳过已显示的信息
		query.include("from");// 同时要把发布人的信息也查找出来，必须要添加此句，否则调用发布人所属类的get方法时得到的将为null
		query.findObjects(this, new FindListener<ShareMood>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("获取心情动态失败！");

			}

			@Override
			public void onSuccess(List<ShareMood> arg0) {
				// TODO Auto-generated method stub
				// 传handler是为了方便在listView中的组件添加点击事件，更新当前Activity的底部评论条
				if (arg0.size() <= 0) {
					ShowToast("当前已经没有更多动态");
					return;
				}
				for (ShareMood sm : arg0) {
					list.add(sm);
				}
				adapter.setList(list);// 会自动更新数据源
				currentPager++;// 页数加1
			}
		});
		xlist_content.stopLoadMore();// 不加此句会一直执行加载更多；
	}

	// 显示软键盘
	// public void showSoftInputView() {
	// if (getWindow().getAttributes().softInputMode ==
	// WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
	// if (getCurrentFocus() != null)
	// ((InputMethodManager)
	// getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(edit_user_comment,
	// 0);
	// }
	// }

}
