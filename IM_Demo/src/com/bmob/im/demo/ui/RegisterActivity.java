package com.bmob.im.demo.ui;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.sms.*;
import cn.bmob.sms.bean.*;
import cn.bmob.sms.exception.*;
import cn.bmob.sms.listener.*;
import cn.bmob.v3.listener.FindListener;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.CommonUtils;


public class RegisterActivity extends BaseActivity {

	Button btn_register,btn_getCheckCode;
	EditText et_checkcode,et_password, et_password2,et_phone;
	RadioButton rb_sex_male,rb_sex_female;
	BmobChatUser currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initTopBarForLeft("注册");
		et_checkcode = (EditText) findViewById(R.id.activity_register_checkcode);
		et_password = (EditText) findViewById(R.id.activity_register_password);
		et_password2 = (EditText) findViewById(R.id.activity_register_password2);
		et_phone = (EditText)findViewById(R.id.activity_register_username);
		
		rb_sex_male=(RadioButton)findViewById(R.id.activity_register_sex_male);
		rb_sex_female=(RadioButton)findViewById(R.id.activity_register_sex_female);
		
		btn_register = (Button) findViewById(R.id.activity_register_button_ok);
		btn_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				register();
			}
		});
		
		btn_getCheckCode=(Button)findViewById(R.id.activity_register_getCheckCode);
		btn_getCheckCode.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String number = et_phone.getText().toString();
				if(!TextUtils.isEmpty(number)){
					BmobSMS.requestSMSCode(RegisterActivity.this, number, "注册模板",new RequestSMSCodeListener() {
						@Override
						public void done(Integer smsId,BmobException ex) {
							// TODO Auto-generated method stub
							if(ex==null){//验证码发送成功
								ShowToast("验证码发送成功，短信id："+smsId);//用于查询本次短信发送详情
							}
						}
					});
				}else{
					ShowToast("请输入手机号码");
				}
			}
		});
		checkUser();
	}
	
	
	private void checkUser(){
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", "smile");
		query.findObjects(this, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if(arg0!=null && arg0.size()>0){
					User user = arg0.get(0);
					user.setPassword("1234567");
					user.update(RegisterActivity.this, new UpdateListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							userManager.login("smile", "1234567", new SaveListener() {
								
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									Log.i("smile", "登陆成功");
								}
								
								@Override
								public void onFailure(int code, String msg) {
									// TODO Auto-generated method stub
									Log.i("smile", "登陆失败："+code+".msg = "+msg);
								}
							});
						}
						
						@Override
						public void onFailure(int code, String msg) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		});
	}
	
	private void register(){
		//取得用户名、密码、确认密码
		String phone = et_phone.getText().toString();
		String password = et_password.getText().toString();
		String pwd_again = et_password2.getText().toString();
		String checkcode = et_checkcode.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}
		if (!pwd_again.equals(password)) {
			ShowToast(R.string.toast_error_comfirm_password);
			return;
		}
		
		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if(!isNetConnected){
			ShowToast(R.string.network_tips);
			return;
		}
		
		
		//验证验证码是否正确
		if(!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(checkcode)){
			BmobSMS.verifySmsCode(RegisterActivity.this,phone,checkcode, 
					new VerifySMSCodeListener() {
				@Override
				public void done(BmobException ex) {
					// TODO Auto-generated method stub
					if(ex==null){//短信验证码已验证成功
						ShowToast("验证通过");
					}else{
						ShowToast("验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
					}
				}
			});
		}

		
		final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
		progress.setMessage("正在注册...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		//由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
		//注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
		final User bu = new User();
		bu.setUsername(phone);
		bu.setPassword(password);
		if(rb_sex_male.isChecked())
		{
			bu.setSex(true);
		}
		else
		{
			bu.setSex(false);
		}
		bu.setPhone(phone);
		//将user和设备id进行绑定aa
		
		bu.setDeviceType("android");
		bu.setInstallId(BmobInstallation.getInstallationId(this));
		bu.signUp(RegisterActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				progress.dismiss();
				ShowToast("注册成功");
				// 将设备与username进行绑定
				userManager.bindInstallationForRegister(bu.getUsername());
				//更新地理位置信息
				updateUserLocation();
				//发广播通知登陆页面退出
				sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
				// 启动主页
				Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
				
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i(arg1);
				ShowToast("注册失败:" + arg1);
				progress.dismiss();
			}
		});
	}
	

	

}
