package common;
/**
 * 信息类型的枚举
 * 
 * @author 寒洲
 * 2020年6月6日
 * 寒洲
 */
public enum MsgType{
	/** 请求登录 */
	LOGIN,
	/** 请求注册 */
	REGISTER,
	/** 请求发送消息 */
	SEND,
	/** 请求发送文件 */
	SEND_FILE,
	/** 请求发送图片 */
	SEND_IMAGE,
	/** 请求获取好友 */
	FRIEND,
	/** 请求添加好友 */
	ADD_FRIEND,
	/** 请求用户退出 */
	LOGOUT,
	/** 请求删除好友 */
	REMOVE,
	
	/** 登录成功 */
	LOGIN_YES,
	/** 登录密码错误 */
	LOGIN_WRONG,
	/** 已登录 */
	LOGINED,
	/** 用户不存在 */
	USER_NOT_FOUND,
	
	/** 注册成功 */
	REGISTER_YES,
	/** 注册失败 */
	REGISTER_ID_EXIST,
	/** 未知原因注册失败 */
	REGISTER_FAIL,
	/** 发送消息成功 */
	SEND_OK,
	/** 同意接收文件 */
	RECEIVE_FILE_YES,
	/** 拒绝接收文件 */
	RECEIVE_FILE_NO,
	/** 文件发送完毕 */
	SEND_FILE_OK,
	/** 可以发送图片 */
	RECEIVE_IMAGE_YES,
	/** 成功发送图片 */
	SEND_IMAGE_OK,
	/** 成功添加好友 */
	FRIEND_ADDED,
	/** 服务器提醒刷新好友列表 */
	REFRESH,
	/** 服务器关闭，强制下线 */
	CLOSE,
}
