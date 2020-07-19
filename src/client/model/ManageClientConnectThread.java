package client.model;

import java.util.HashMap;


/**
 * 管理客户端与服务器连接的线程
 * Client --> Server
 * @author 寒洲
 * 2020年6月10日
 * 寒洲
 */
public class ManageClientConnectThread {

	/** 储存已连接到服务器的客户端线程 */
	private static HashMap<Long, ClientConnectServerThread> onlineUserThread = new HashMap<Long, ClientConnectServerThread>();
	/**
	 * 储存客户端与服务器连接的线程
	 * 
	 * @param friendID
	 * @param ccst
	 */
	public static void setClientConServerThread(Long userID,ClientConnectServerThread ccst) {
		onlineUserThread.put(userID, ccst);
	}
	/**
	 * 取得客户端与服务器连接的线程
	 * 
	 * @param friendID
	 * @return
	 */
	public static ClientConnectServerThread getClientConServerThread(Long userID) {
		return (ClientConnectServerThread)onlineUserThread.get(userID);
	}
}
