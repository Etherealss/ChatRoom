package client.model;

/**
 * 管理用户聊天界面对象
 * 
 * @author 寒洲
 * 2020年6月13日
 * 寒洲
 */

import java.util.HashMap;

import client.ui.ChatFrame;

public class ManageChatFrame {

	private static HashMap<String, ChatFrame> hm = new HashMap<String, ChatFrame>();

	/**
	 * 获取聊天界面对象
	 * 
	 * @param IDs <br>
	 *            IDs即当前用户ID和聊天对象ID合在一起的String。<br>
	 *            在字符串前面的就是要获取的用户ID
	 * @return 对应当前用户的ChatFrame
	 */
	public static ChatFrame getChatFrame(String IDs) {
		return (ChatFrame) hm.get(IDs);
	}

	/**
	 * 储存聊天界面对象
	 * 
	 * @param IDs       当前用户ID和聊天对象ID合在一起的String
	 * @param chatFrame 选择的聊天对象的ChatFrame
	 */
	public static void addChatFrame(String IDs, ChatFrame chatFrame) {
		hm.put(IDs, chatFrame);
	}
	/**
	 * 退出聊天界面，移出聊天界面对象
	 * 
	 * @param IDs
	 */
	public static void remove(String IDs) {
		hm.remove(IDs);
	}
}
