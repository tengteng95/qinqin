package com.bmob.im.demo.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Note extends BmobObject {
	BmobChatUser from;//谁发送的便利贴
	BmobRelation to;//发送给谁（可以是单个人也可以是多个人）
	int year,month,day,hour,second;//提醒时间
	String content;//提醒的内容
}
