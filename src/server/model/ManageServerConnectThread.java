package server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 管理服务器线程与客户端连接的线程 Server --> Client
 * 
 * @author 寒洲 2020年6月7日 寒洲
 */
public class ManageServerConnectThread {

	/** 储存已连接到某个客户端的服务器线程 */
	public static HashMap<Long, ServerConnectThread> onlineUserThread = new HashMap<Long, ServerConnectThread>();

	/**
	 * 添加服务器线程。<br>
	 * 该线程为服务器与客户端保持连接的线程
	 * 
	 * @param userID
	 * @param sct
	 */
	public static void addClientThread(Long userID, ServerConnectThread sct) {
		onlineUserThread.put(userID, sct);
	}

	/**
	 * 返回服务器与输入的用户ID连接的线程<br>
	 * 用于发送信息
	 * 
	 * @param userID
	 * @return the ServerConnectThread of the userID
	 */
	public static ServerConnectThread getClientThread(Long userID) {
		return (ServerConnectThread) onlineUserThread.get(userID);
	}
	/**
	 * 用户退出，移出HashMap
	 * 
	 * @param userID
	 */
	public static void remove(Long userID) {
		onlineUserThread.remove(userID);
	}
	/**
	 * 返回在线用户的ID，用ArrayList储存
	 * 
	 * @return
	 */
	public static List<Long> getOnlineUserID() {
		//获取HashMap中的key值，即获取在线用户的ID
		Iterator<Long> it = onlineUserThread.keySet().iterator();
		List<Long> onlineUserIDList = new ArrayList<Long>();
		while (it.hasNext()) {
			String s = it.next().toString();
			onlineUserIDList.add(Long.parseLong(s));
		}
		return onlineUserIDList;
	}
	
	/**
	 * 返回与在线用户的通讯线程，用ArrayList储存
	 * 
	 * @return
	 */
	public static List<ServerConnectThread> getOnlineUserThread() {
		//获取HashMap中的value，即获取与在线用户的通讯线程
		List<ServerConnectThread> onlineUserThreads = new ArrayList<ServerConnectThread>();
		//通过迭代器获取在线用户ID，再由ID获取线程
		Iterator<Long> it = onlineUserThread.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next().toString();
			Long ID = Long.parseLong(s);
			onlineUserThreads.add(onlineUserThread.get(ID));
		}
			
		return onlineUserThreads;
	}
}
