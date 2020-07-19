package common;

import java.io.Serializable;

/**
 * 用户信息类
 * 
 * @author 寒洲
 * @date:2020年5月27日
 */
public class User implements Serializable{
	/**  */
	private static final long serialVersionUID = 3681673122583797750L;
	/** 账号 */
	private Long ID;
	/** 密码 */
	private String password;
	/** 昵称 */
	private String nickname;
	/** 头像 */
	private String avatarPath;
	/** 性别 */
	private String sex;
	/** 用户默认存储位置 */
	private String defaultPath ;
	/** 用户在线状态，true为在线 */
	private Boolean onlineState = false;
	
	//TODO 不支持自定义存储位置
//	/**
//	 * @param 将储存位置设置为默认"D:/ChatRoom/" + ID
//	 */
//	public void setDefaultPath() {
//		this.defaultPath = "D:/ChatRoom/" + ID;
//	}

	/**
	 * 创建一个User对象
	 * @param ID
	 * @param password
	 */
	public User(Long ID,String password) {
		this.ID = ID;
		this.password = password;
		//TODO 设置默认存储位置
		defaultPath = "D:/ChatRoom/" + ID;
	}
	
	/**
	 * @return 用户在线状态，true为在线
	 */
	public Boolean getOnlineState() {
		return onlineState;
	}

	/**
	 * @param 用户在线状态，true为在线
	 */
	public void setOnlineState(Boolean onlineState) {
		this.onlineState = onlineState;
	}

	/**
	 * @return 用户默认存储位置
	 */
	public String getDefaultPath() {
		return defaultPath;
	}
	
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * @return 用户头像路径
	 */
	public String getAvatarPath() {
		return avatarPath;
	}

	/**
	 * @param 用户头像路径
	 */
	public void setAvatarPath(String avatar) {
		avatarPath = avatar;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	@Override
    public String toString() {
        return this.getClass().getName()
                + "[id=" + this.ID
                + ",pwd=" + this.password
                + ",nickname=" + this.nickname
                + ",head=" + this.avatarPath
                + ",sex=" + this.sex
                + ",onlineState=" + this.onlineState
                + "]";
    }
	
	public User() {}
}
