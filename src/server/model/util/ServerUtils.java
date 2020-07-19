package server.model.util;

import java.io.IOException;
import java.util.List;

import common.MsgType;
import common.Request;
import server.model.ManageServerConnectThread;
import server.model.ServerConnectThread;

/**
 * 服务器工具类
 * 
 * @author 寒洲 2020年6月17日 寒洲
 */
public class ServerUtils {

	/**
	 * 向在线用户发送广播
	 * 
	 * @param feedback
	 */
	public static void broadcast(Request feedback) {
		List<ServerConnectThread> onlineUserThreads = ManageServerConnectThread.getOnlineUserThread();

		if (feedback.getFeedback() == MsgType.CLOSE) {
			for (int i = 0; i < onlineUserThreads.size(); i++) {
				ServerConnectThread sct = onlineUserThreads.get(i);
				try {
					if (sct != null) {
						sct.getOosFeedback().writeObject(feedback);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
