package server.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import client.MySocket;
import common.Request;
import common.User;
import server.MyServer;
import server.model.util.ServerUtils;
import server.ui.MyServerFrame;
import common.MsgType;

/**
 * 服务器与客户端保持通讯的线程。同时管理了客户端流
 * 
 * @author 寒洲 2020年6月6日 寒洲
 */
public class ServerConnectThread extends Thread {
	/** 接收客户端请求的流 */
	private ObjectInputStream ois = null;
	/** 服务器向客户端反馈的流 */
	private ObjectOutputStream oosFeedback = null;
	/** 客户端转发信息给接收者的流 */
	private ObjectOutputStream oosSend = null;
	/** 与数据库的连接 */
	private ServerConnectMySQL scm = null;
	/** 服务器界面 */
	private MyServerFrame serverFrame = null;

	/**
	 * 获得与客户端连接的服务器线程管理的反馈流
	 * 
	 * @return the oosFeedback
	 */
	public ObjectOutputStream getOosFeedback() {
		return oosFeedback;
	}

	/**
	 * 传入与服务器连接的客户端，同时传入有关的对象
	 * 
	 * @param socket
	 * @param ois    与客户端连接的读取流
	 * @param oos    与客户端连接的输出流
	 * @param scm    与数据库的连接
	 */
	public ServerConnectThread(ObjectInputStream ois, ObjectOutputStream oos, 
			ServerConnectMySQL scm, MyServerFrame serverFrame) {
		this.ois = ois;
		this.scm = scm;
		this.oosFeedback = oos;
		this.serverFrame = serverFrame;
	}

	@Override
	public void run() {
		// 用户是否连接。如果用户请求退出，则将其设置为false
		Boolean isConnected = true;
		// 持续接收信息
		try {
			while (isConnected) {
				// 客户端的请求/发送的消息
				Request request = (Request) ois.readObject();

				System.out.println("ServerConnectThread:客户端的请求:" + request.getRequestion());
				switch (request.getRequestion()) {
				// 发送文字、图片和文件都是同一种处理方式
				case SEND_IMAGE:
				case SEND_FILE:
				case SEND: {
					// 客户端请求发送信息
					// 将反馈设置为请求的类型，由客户端接收并判断类型
					request.setFeedback(request.getRequestion());
					/*
					 * 转发给接收者 获取与接收者连接的客户端线程 在登录的时候已经将于客户端连接的线程传入HashMap中了，
					 * 此时可以通过获取线程来获取客户端与服务器通讯的所有流
					 */
					ServerConnectThread sct = 
							ManageServerConnectThread.getClientThread(request.getRecipientID());
					/*
					 * 通过线程 获取线程管理的反馈流 服务器发送的消息对于客户端来说都是以反馈Feedback的形式接收的
					 */
					// 发给接收者的流
					oosSend = sct.getOosFeedback();
					oosSend.writeObject(request);
					// 保存记录
//					ServerSaveRecord save = new ServerSaveRecord();
					break;
				}
				case REFRESH: {
					// 客户端请求刷新好友列表
				}
				case FRIEND: {
					// 客户端请求获取好友列表
					// 通过数据库获取好友的User对象，用List储存
					List<User> friends = scm.getFriends(request.getSenderID());

					// 获取在线用户
					final List<Long> onlineUserList = ManageServerConnectThread.getOnlineUserID();

					// 设置好友的在线状态
					for (int i = 0; i < friends.size(); i++) {
						User user = friends.get(i);
						for (int j = 0; j < onlineUserList.size(); j++) {
							// 如果好友ID与在线用户ID匹配则表示好友在线
							if (user.getID().equals(onlineUserList.get(j))) {
								// 这里要改变friends列表中的User对象
								friends.get(i).setOnlineState(true);
							}
						}
					}
					Request feedback = new Request();
					feedback.setFriends(friends);

					feedback.setFeedback(MsgType.FRIEND);
					oosFeedback.writeObject(feedback);
					break;
				}
				case REMOVE:
				case ADD_FRIEND: {
					Long senderID = request.getSenderID();
					Long targetID = request.getRecipientID();
					String senderNickname = request.getSenderNickname();
					// 反馈给发送者
					Request feedback = new Request();
					// 此处要添加目标ID，如果在删除好友时仍与该好友聊天的话，需要这个ID来调用Manage关闭聊天窗口
					feedback.setSenderID(targetID);

					// 反馈给被添加/删除者
					Request feedback2 = new Request();
					// 改变好友关系
					MsgType msgType = scm.changeRelationship(senderID, targetID,
								request.getRequestion());
					// 若客户端请求的是添加好友，返回添加好友的结果
					if (request.getRequestion() == MsgType.ADD_FRIEND) {
						if (msgType == MsgType.FRIEND_ADDED) {
							// 成功添加好友
							feedback.setFeedback(MsgType.FRIEND_ADDED);
							// 反馈给被添加者：被添加
							feedback2.setRequestion(MsgType.ADD_FRIEND);
							feedback2.setContent(senderNickname + "[" + senderID + "]添加你为好友");
						} else {
							// 用户不存在
							feedback.setFeedback(MsgType.USER_NOT_FOUND);
						}
					} else {
						// 若客户端请求的是删除好友，反馈结果
						if (msgType == MsgType.REMOVE) {
							// 成功删除好友
							feedback.setFeedback(MsgType.REMOVE);

							feedback2.setSenderID(senderID);
							feedback2.setRecipientID(targetID);
							feedback2.setRequestion(MsgType.REMOVE);
							feedback2.setContent(senderNickname + "[" + senderID + "]与你解除好友关系");
							// 反馈给被删除方：被删除
						}
						// 删除好友不会发生用户不存在的情况，因为只能选择好友删除
					}
					// 提醒被添加/删除方刷新好友
					feedback2.setFeedback(MsgType.REFRESH);
					ServerConnectThread targetUser = 
							ManageServerConnectThread.getClientThread(targetID);
					targetUser.getOosFeedback().writeObject(feedback2);
					oosFeedback.writeObject(feedback);
					break;
				}
				case RECEIVE_IMAGE_YES:
				case RECEIVE_FILE_YES: {
					// 反馈给文件或者图片发送者：可以发送
					request.setFeedback(request.getRequestion());
					// 获取发送者的线程
					ServerConnectThread sct = 
							ManageServerConnectThread.getClientThread(request.getSenderID());
					// 获取反馈流oosFeedback然后发送
					sct.getOosFeedback().writeObject(request);
					break;
				}
				case RECEIVE_FILE_NO: {
					// 拒绝接收文件
					// 反馈给文件发送者：拒绝接收文件
					request.setFeedback(MsgType.RECEIVE_FILE_NO);
//					}
					// 获取发送者的线程
					ServerConnectThread sct = 
							ManageServerConnectThread.getClientThread(request.getSenderID());
					// 获取反馈流oosFeedback然后发送
					sct.getOosFeedback().writeObject(request);
					break;
				}
				case LOGOUT: {
					// 客户端退出程序
					// 移出哈希表
					Long logoutID = request.getSenderID();
					ManageServerConnectThread.remove(logoutID);

					// 移出在线用户列表
					serverFrame.removeLogoutUser(logoutID);

					// 不再监听
					isConnected = false;
					// 设置反馈，让客户端线程自行关闭
					request.setFeedback(MsgType.LOGOUT);
					oosFeedback.writeObject(request);

					// 通知其好友，该用户下线，同时刷新好友列表
					// 此时requestion为MsgType.LOGOUT，可用于在ccst判断上下线类型
					request.setFeedback(MsgType.REFRESH);

					// 好友下线提示
					request.setContent("你的好友：" + request.getSenderNickname() +
									"[" + logoutID + "]下线了喔");

					// 向好友广播
					broadcastToFriend(request);
					// 关闭与该客户端的连接流
					if (ois != null) {
						ois.close();
					}
					if (oosFeedback != null) {
						oosFeedback.close();
					}
					break;
				}
				default:
					System.out.println("ServerConnectClientThread:收到其他消息");
					break;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			//关闭流
			closeAll();
		}
	}

	/**
	 * 关闭服务器的流
	 */
	public void closeAll() {
		if (ois != null) {
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (oosFeedback != null) {
			try {
				oosFeedback.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 因为要获取用户的好友列表所以放在这个类中
	/**
	 * 向用户的好友发送广播
	 * 
	 * @param feedback SenderID包含了用户的ID号
	 */
	public void broadcastToFriend(Request feedback) {
		// 获取在线用户
		List<Long> onlineUsersID = ManageServerConnectThread.getOnlineUserID();
		// 判断是否存在在线用户
		// 如果不存在就不用发了
		if (onlineUsersID != null) {
			// 获取用户的好友列表
			List<User> friends = scm.getFriends(feedback.getSenderID());
			// 通过两个List的比较识别好友中的在线用户，并发送下线提醒
			for (int i = 0; i < friends.size(); i++) {
				Long friendID = friends.get(i).getID();

				for (int j = 0; j < onlineUsersID.size(); j++) {
					// 如果好友中有在线用户
					if (friendID.equals(onlineUsersID.get(j))) {
						// 获取在线用户的通讯线程
						ServerConnectThread sct = ManageServerConnectThread.
									getClientThread(friendID);
						try {
							// 向在线用户发送提醒
							sct.getOosFeedback().writeObject(feedback);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}
	}
}
