package com.bmob.im.demo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.MyMessageReceiver;
import com.bmob.im.demo.R;
import com.bmob.im.demo.config.myBmobConfig;
import com.bmob.im.demo.ui.fragment.ContactFragment;
import com.bmob.im.demo.ui.fragment.FunctionFragment;
import com.bmob.im.demo.ui.fragment.RecentFragment;
import com.bmob.im.demo.ui.fragment.SettingsFragment;

/**
 * 登陆
 * @ClassName: MainActivity
 * @Description: TODO
 * @author smile
 * @date 2014-5-29 下午2:45:35
 */
public class MainActivity extends ActivityBase implements EventListener{

	private Button[] mTabs;
	private FunctionFragment functionFragment;//其他功能界面
	private ContactFragment contactFragment;//联系人界面
	private RecentFragment recentFragment;//消息界面
	private SettingsFragment settingFragment;//设置界面
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;//当前所在界面索引
	
	ImageView iv_fun_tips,iv_recent_tips,iv_contact_tips;//新消息提示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		//如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
		BmobChat.getInstance(this).startPollService(30);//原先被设为注释，即去掉了
		
		//开启广播接收器
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		
		initNewDrawBoardBroadCast();
		initView();
		initTab();
	}

	private void initView(){
		mTabs = new Button[4];
		mTabs[0] = (Button) findViewById(R.id.btn_fun);
		mTabs[1] = (Button) findViewById(R.id.btn_message);
		mTabs[2] = (Button) findViewById(R.id.btn_contract);
		mTabs[3] = (Button) findViewById(R.id.btn_set);
		iv_fun_tips = (ImageView)findViewById(R.id.iv_fun_tips);
		iv_recent_tips = (ImageView)findViewById(R.id.iv_recent_tips);
		iv_contact_tips = (ImageView)findViewById(R.id.iv_contact_tips);
		//把第一个tab设为选中状态
		mTabs[0].setSelected(true);
	}
	
	private void initTab(){
		functionFragment=new FunctionFragment();
		contactFragment = new ContactFragment();
		recentFragment = new RecentFragment();
		settingFragment = new SettingsFragment();
		fragments = new Fragment[] {functionFragment,recentFragment, contactFragment, settingFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction()
		    .add(R.id.fragment_container,functionFragment)
		    .add(R.id.fragment_container, recentFragment)
			.add(R.id.fragment_container, contactFragment)
			.add(R.id.fragment_container,settingFragment)
			.hide(contactFragment)
			.hide(settingFragment)
			.hide(recentFragment)
			.show(functionFragment).commit();
	}
	
	
	
	/**
	 * button点击事件
	 * @param view
	 */
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.btn_fun:
			index = 0;
			break;
		case R.id.btn_message:
			index = 1;
			break;
		case R.id.btn_contract:
			index = 2;
			break;
		case R.id.btn_set:
			index = 3;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		//把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//小圆点提示
		//判断是否有未读消息
		if(BmobDB.create(this).hasUnReadMsg()){
			iv_recent_tips.setVisibility(View.VISIBLE);
		}else{
			iv_recent_tips.setVisibility(View.GONE);
		}
		//判断是否有新邀请
		if(BmobDB.create(this).hasNewInvite()){
			iv_contact_tips.setVisibility(View.VISIBLE);
		}else{
			iv_contact_tips.setVisibility(View.GONE);
		}
		
		//判断是否有新的自定义消息（因为无法自定义BmobDB类型，所以只能通过联网查询的方式
		
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		//清空
		MyMessageReceiver.mNewNum=0;
		
	}
	
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}
	
	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		refreshNewMsg(message);
	}
	
	
	/** 刷新界面
	  * @Title: refreshNewMsg
	  * @Description: TODO
	  * @param @param message 
	  * @return void
	  * @throws
	  */
	private void refreshNewMsg(BmobMsg message){
		// 声音提示
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_recent_tips.setVisibility(View.VISIBLE);
		//也要存储起来
		if(message!=null){
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true,message);
		}
		if(currentTabIndex==1){
			//当前页面如果为会话页面，刷新此页面
			if(recentFragment != null){
				recentFragment.refresh();
			}
		}
	}
	
	NewBroadcastReceiver  newReceiver;
	
	private void initNewMessageBroadCast(){
		// 注册接收消息广播
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		//优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}
	
	
	private class NewDrawBoardBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Toast.makeText(MainActivity.this, "new board", Toast.LENGTH_LONG).show();
			//刷新界面
			refreshNewDrawBoard(null);
			// 记得把广播给终结掉
			abortBroadcast();
			
		}
	}
	
	private void refreshNewDrawBoard(BmobMsg message){
		// 声音提示
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_fun_tips.setVisibility(View.VISIBLE);
		//也要存储起来
		/*if(message!=null){
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true,message);
		}*/
		if(currentTabIndex==0){
			//当前页面如果为功能页面，刷新此页面
			if(functionFragment != null){
				functionFragment.refresh();
			}
		}
	}
	NewDrawBoardBroadcastReceiver drawboardReceiver;
	void initNewDrawBoardBroadCast()
	{
		drawboardReceiver=new NewDrawBoardBroadcastReceiver();
		IntentFilter intentf=new IntentFilter(myBmobConfig.FUN_DRAWBOARD);
		registerReceiver( drawboardReceiver,intentf);
	}
	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//刷新界面
			refreshNewMsg(null);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}
	
	TagBroadcastReceiver  userReceiver;
	
	private void initTagMessageBroadCast(){
		// 注册接收消息广播
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		//优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}
	
	/**
	 * 标签消息广播接收者
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
			refreshInvite(message);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if(isNetConnected){
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		// TODO Auto-generated method stub
		refreshInvite(message);
	}
	
	/** 刷新好友请求
	  * @Title: notifyAddUser
	  * @Description: TODO
	  * @param @param message 
	  * @return void
	  * @throws
	  */
	private void refreshInvite(BmobInvitation message){
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_contact_tips.setVisibility(View.VISIBLE);
		if(currentTabIndex==2){
			if(contactFragment != null){
				contactFragment.refresh();
			}
		}else{
			//同时提醒通知
			String tickerText = message.getFromname()+"请求添加好友";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),NewFriendActivity.class);
		}
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		showOfflineDialog(this);
	}
	
	@Override
	public void onReaded(String conversionId, String msgTime) {
		// TODO Auto-generated method stub
	}
	
	
	private static long firstTime;
	/**
	 * 连续按两次返回键就退出
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("再按一次退出程序");
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}
		//取消定时检测服务
//		BmobChat.getInstance(this).stopPollService();
	}
	
}
