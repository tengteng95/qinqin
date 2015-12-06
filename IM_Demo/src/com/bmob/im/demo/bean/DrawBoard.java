package com.bmob.im.demo.bean;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;
public class DrawBoard extends BmobObject{

	private String content;//公告内容
	private BmobChatUser from;//发布者
	private BmobRelation to;//可以看到公告的人（发布者的所有亲人）
	
	public void setContent(String content)
	{
		this.content=content;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public void setUserFrom(BmobChatUser user)
	{
		from=user;
	}
	
	public BmobChatUser getUserFrom()
	{
		return from;
	}
	
	public void setUserTo(BmobRelation to)
	{
		this.to=to;
	}
	
	public BmobRelation getUserTo()
	{
		return to;
	}
	
}
