package com.bmob.im.demo.bean;

import java.io.Serializable;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class ShareMood extends BmobObject implements Serializable{
	private BmobChatUser from;//发布人
	private BmobRelation to;//可以看到此动态的人
	private String content;//文本内容
	private BmobFile img;//非文本内容（图片）
	private BmobRelation like;//点赞的人
	private BmobRelation comments;//评论
	
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
		this.to = to;
	}
	
	public BmobRelation getUserTo()
	{
		return to;
	}
	
	public void setContent(String content)
	{
		this.content=content;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public void setWhoLikes(BmobRelation likes)
	{
		this.like=likes;
	}
	
	public BmobRelation getWhoLikes()
	{
		return like;
	}
	
	public void setComments(BmobRelation comments)
	{
		this.comments=comments;
	}
	
	public BmobRelation getComments()
	{
		return comments;
	}

}
