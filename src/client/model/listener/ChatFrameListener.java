package client.model.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import client.model.ManageClientConnectThread;
import client.ui.ChatFrame;
import common.Request;
import common.FileInfo;
import common.MsgType;

/**
 * ChatFrame的监听器
 * 
 * @author 寒洲
 * 2020年6月10日
 * 寒洲
 */
public class ChatFrameListener implements ActionListener {

	private ChatFrame chatFrame = null;

	public ChatFrameListener(ChatFrame chatFrame) {
		this.chatFrame = chatFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 信息包的基本信息
		Request req = new Request();
		req.setSenderID(chatFrame.getUserID());
		req.setSenderNickname(chatFrame.getUser().getNickname());
		req.setRecipientID(chatFrame.getFriendID());
		req.setDate(new Date());
		// 判断按钮
		switch (e.getActionCommand()) {
		case "发送": {
			sendMsg(req);
			break;
		}
		case "图片": {
			sendImage(req);
			break;
		}
		case "文件": {
			sendFile(req);
			break;
		}
		case "历史记录": {
			// TODO 历史记录
			break;
		}

		default:
			break;
		}
	}

	/**
	 * 发送信息
	 */
	private void sendMsg(Request req) {
		// 获取输入的信息
		String content = chatFrame.getJtpChat().getText();
		// 判断输入是否为空
		if ("".equals(content)) {
			// 如果输入为空，弹出提示框
			chatFrame.createDialog("信息不能为空");
		} else {
			// 发送消息
			req.setRequestion(MsgType.SEND);
			req.setContent(content);
			// 获取该客户端与服务器连接的通讯线程，通过其下的sendInfoServer方法发送
			ManageClientConnectThread.getClientConServerThread(
						chatFrame.getUserID()).sendInfoServer(req);
			// 清空输入框
			chatFrame.getJtpChat().setText("");
			// 将信息显示在信息显示窗口上
			req.setFeedback(MsgType.SEND_OK);
			chatFrame.setMessage(req);
		}
	}

	/**
	 * 选择和发送图片
	 */
	private void sendImage(Request req) {
		// 文件选择对话框
		JFileChooser chooser = new JFileChooser();
		// 文件名后缀过滤器
		FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("图片文件", 
					"jpg", "jpeg", "png", "gif");
		// 图片后缀的正则表达式
		String pattern = ".+(.jpeg|.jpg|.png|.gif)$";
		// 待会儿用这个引用得到桌面路径
		FileSystemView fsv = FileSystemView.getFileSystemView();

		System.out.println("发送图片按钮按下");
		// 设置过滤器
		chooser.setFileFilter(imageFilter);
		// 设置桌面为默认路径
		chooser.setCurrentDirectory(fsv.getHomeDirectory());
		// 显示对话框
		int ret = chooser.showOpenDialog(chatFrame);
		// 获取用户选择的结果
		if (ret == JFileChooser.APPROVE_OPTION) {
			// 选择已经存在的一个图片
			String imagePath = chooser.getSelectedFile().getAbsolutePath();
			if (imagePath.matches(pattern)) {
				// 选择了图片
				req.setRequestion(MsgType.SEND_IMAGE);

				FileInfo imageInfo = getFileInfo(imagePath);
				req.setFileInfo(imageInfo);
				// 获取该客户端与服务器连接的通讯线程，通过其下的sendInfoServer方法发送
				ManageClientConnectThread.getClientConServerThread(
							chatFrame.getUserID()).sendInfoServer(req);
			} else {
				// 不支持的图片格式弹窗
				chatFrame.createDialog("不支持的图片格式！可以试试用发送文件的方式嗷");
			}
		}
	}

	/**
	 * 选择和发送文件
	 */
	private void sendFile(Request req) {
		// 图片后缀的正则表达式
		String pattern = ".+(.jpeg|.jpg|.png|.gif)$";
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("常用文件",
					"txt", "doc", "docx", "xls", "xlsx","xml", "ppt", "pptx");
		// ----文件按钮监听器----
		JFileChooser chooser = new JFileChooser();
		// 设置过滤器
		chooser.setFileFilter(fileFilter);
		// 待会儿用这个引用得到桌面路径
		FileSystemView fsv = FileSystemView.getFileSystemView();
		// 设置桌面为默认路径
		chooser.setCurrentDirectory(fsv.getHomeDirectory());
		// 显示对话框
		int ret = chooser.showOpenDialog(chatFrame);
		// 获取用户选择的结果
		if (ret == JFileChooser.APPROVE_OPTION) {
			// 选择已经存在的一个文件,并获取它的绝对路径,用@file:%标记
			String filePath = chooser.getSelectedFile().getAbsolutePath();
			// 判断是文件还是图片
			if (filePath.matches(pattern)) {
				// 是图片
				req.setRequestion(MsgType.SEND_IMAGE);
			} else {
				// 是文件
				req.setRequestion(MsgType.SEND_FILE);
			}
			// 创建发送文件的信息类，加入必要的信息
			FileInfo fileInfo = getFileInfo(filePath);

			req.setFileInfo(fileInfo);
			// 获取该客户端与服务器连接的通讯线程，通过线程中的方法发送
			ManageClientConnectThread.getClientConServerThread(
						chatFrame.getUserID()).sendInfoServer(req);
		}
	}

	/**
	 * 设置文件信息类，图片视为特殊的文件处理
	 * 
	 * @param filePath
	 * @return FileInfo
	 */
	private FileInfo getFileInfo(String filePath) {
		// 创建发送文件的信息类，加入必要的信息
		FileInfo fileInfo = new FileInfo();
		// 源文件路径，在收到同意接收后会用到这个路径获取文件
		fileInfo.setSrcFilePath(filePath);
		// 文件名，用于提示接收者
		String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
		fileInfo.setFileName(fileName);
		return fileInfo;
	}
}
