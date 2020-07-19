package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.MsgType;
/**
 * 服务器反馈信息
 * 
 * @author 寒洲
 * 2020年6月2日
 * 寒洲
 */
public class Request implements Serializable{

	/**  */
	private static final long serialVersionUID = 418567926986314728L;
	
	/** 客户端请求 */
	private MsgType requestion = null;
	/** 服务器反馈 */
	private MsgType feedback = null;
	/** 发送内容 */
	private String content = null;
	/** 发送者 */
	private Long senderID = 0L;
	/** 发送者的昵称 */
	private String senderNickname = null;
	/** 接收者 */
	private Long recipientID = 0L;
	/** 发送日期 */
	private Date date = null;
	/** 登录时传输的用户对象 */
	private User user = null;
	/** 发送文件时的信息类 */
	private FileInfo fileInfo = null;
	/** 好友列表 */
	private List<User> friends = new ArrayList<User>();
	/**
	 * @return 文件信息类
	 */
	public FileInfo getFileInfo() {
		return fileInfo;
	}
	/**
	 * @param 文件信息类
	 */
	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
	/**
	 * @return 反馈信息类型
	 */
	public MsgType getFeedback() {
		return feedback;
	}
	/**
	 * @param 反馈信息类型
	 */
	public void setFeedback(MsgType feedback) {
		this.feedback = feedback;
	}
	
	/**
	 * @return 好友列表
	 */
	public List<User> getFriends() {
		return friends;
	}
	/**
	 * @param 好友列表
	 */
	public void setFriends(List<User> friends) {
		this.friends = friends;
	}
	/**
	 * @return User
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param User
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return 日期和时间
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param 日期和时间
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return 内容
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param 内容
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * @return 发送者ID
	 */
	public Long getSenderID() {
		return senderID;
	}
	/**
	 * @param 发送者ID
	 */
	public void setSenderID(Long senderID) {
		this.senderID = senderID;
	}
	
	/**
	 * @return 接收者ID
	 */
	public Long getRecipientID() {
		return recipientID;
	}
	/**
	 * @param 接收者ID
	 */
	public void setRecipientID(Long recipientID) {
		this.recipientID = recipientID;
	}
	
	/**
	 * @return 请求类型
	 */
	public MsgType getRequestion() {
		return requestion;
	}
	/**
	 * @param 请求类型
	 */
	public void setRequestion(MsgType reqType) {
		this.requestion = reqType;
	}
	/**
	 * 发送者昵称
	 * @return
	 */
	public String getSenderNickname() {
		return senderNickname;
	}
	/**
	 * 发送者昵称
	 * @param senderNickname
	 */
	public void setSenderNickname(String senderNickname) {
		this.senderNickname = senderNickname;
	}
}

