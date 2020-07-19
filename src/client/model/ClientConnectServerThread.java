package client.model;

import common.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import client.MySocket;
import client.model.util.SavaFileUtils;
import client.ui.ChatFrame;
import client.ui.MainFrame;
import client.ui.util.MyDialog;

/**
 * 客户端与服务器连接的线程
 * 
 * @author 寒洲 2020年6月10日 寒洲
 */
public class ClientConnectServerThread extends Thread {
	/** 客户端的套接字 */
	private MySocket socket = null;

	/** 默认储存账号数据文件的位置 */
	private String defaultPath;
	/** 用户ID */
	private Long userID;

	/**
	 * @param 传入user供线程获取需要的对象
	 */
	public void setting(User user) {
		defaultPath = user.getDefaultPath();
		userID = user.getID();
	}

	/**
	 * @return the socket
	 */
	public MySocket getSocket() {
		return socket;
	}

	public ClientConnectServerThread(MySocket socket) {
		this.socket = socket;
	}

	/**
	 * 请求发送信息至服务器
	 */
	public void sendInfoServer(Request request) {
		try {
			socket.oos.writeObject(request);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 持续接收服务器的消息
	 */
	@Override
	public void run() {
		try {
			while (socket.isConnected() && !socket.isClosed()) {
				// 接收到的信息
				Request receive = (Request) socket.ois.readObject();

				System.out.println("ccst:run()：接收服务器的信息:" + receive.getFeedback());

				switch (receive.getFeedback()) {
				case SEND: {
					/*
					 * 服务器转发来好友信息 通过ManageChatFrame.getChatFrame(IDs)获取接收者的ChatFrame 通过方法实现
					 */
					ChatFrame chatFrame = getFriendChatFrame(receive);
					// 调用setMessage显示信息
					chatFrame.setMessage(receive);
					break;
				}
				case REFRESH: {
					// 服务器通知：刷新好友列表
					System.out.println("ccst:刷新了好友列表");
					// 获取主窗口对象，然后通过工具类创建对话框
					MainFrame mainFrame = socket.getMainFrame();
					MyDialog.showDialog(mainFrame, receive.getContent());
					// 好友上、下线提示
					if (receive.getRequestion() == MsgType.LOGOUT) {
						// 同时，如果有人正与下线用户聊天，因为不支持和离线好友聊天所以好关闭聊天窗口
						closeChatFrame(receive, "该好友已下线！");
					} else if (receive.getRequestion() == MsgType.REMOVE) {
						// 如果删除的时候保持着与该用户的聊天窗口 、则关闭
						closeChatFrame(receive, "已删除该好友!");
					}
					// 再次获取好友，刷新好友列表
					refreshFriend(userID);
					break;
				}
				case FRIEND: {
					// 服务器发来好友列表
					System.out.println("ccst:获取了服务器反馈的好友列表！ ");
					// 刷新好友列表
					List<User> friends = receive.getFriends();
					// 没有好友就不刷新
					if (friends != null) {
						socket.getMainFrame().setFriendPanel(friends);
					}
					break;
				}
				case FRIEND_ADDED: {
					// 窗口弹出“成功添加好友”对话框然后关闭
					MyDialog.showDialog(socket.getMainFrame(), "成功添加好友！");
					// 刷新好友列表
					refreshFriend(userID);
					break;
				}
				case REMOVE: {
					// 窗口弹出“成功删除好友”对话框然后关闭
					MyDialog.showDialog(socket.getMainFrame(), "成功删除好友！");
					closeChatFrame(receive, "该好友已被删除！");
					// 刷新好友列表
					refreshFriend(userID);
					break;
				}
				case USER_NOT_FOUND: {
					// 窗口弹出“好友不存在”对话框
					MainFrame mainFrame = socket.getMainFrame();
					mainFrame.createDialog("用户不存在！");
					// 添加好友失败
					if (receive.getRequestion() == MsgType.ADD_FRIEND) {
						// 添加失败时，再次点击添加好友按钮，可以继续输入
						socket.getMainFrame().getJbAddFriend().doClick();
					} else {
						// 删除好友失败
						socket.getMainFrame().getJbRemove().doClick();
					}
					break;
				}
				case SEND_FILE: {
					// 有人请求给我发送文件
					chooseAcceptFile(receive);
					break;
				}
				case RECEIVE_IMAGE_YES: {
					// 和接收文件一样
				}
				case RECEIVE_FILE_YES: {
					// 发送文件或者图片，传入信息
					sendFile(receive);
					break;
				}
				case RECEIVE_FILE_NO: {
					// 对方拒绝接收文件！
					ChatFrame chatFrame = getFriendChatFrame(receive);
					chatFrame.createDialog("对方拒绝接收文件！嘤嘤嘤");
					break;
				}
				case SEND_IMAGE: {
					// 直接接收图片
					prepareReceiveImage(receive);
					break;
				}
				case LOGOUT: {
					// 用户退出，关闭Socket
					socket.closeAll();
					// 关闭程序
					System.out.println("退出");
					System.exit(0);
					break;
				}
				case CLOSE: {
					// TODO 强制下线
					socket.getMainFrame().createDialog("服务器关闭！强制下线！");
					socket.closeAll();
					// 关闭
					System.out.println("强制下线");
					System.exit(0);
//					break;
				}
				default:
					break;
				}
			}
		} catch (SocketException e) {
			// TODO 掉线提醒
			socket.getMainFrame().createDialog("与服务器的异常中断!请退出后重试！");
			e.printStackTrace();
			System.exit(0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭与好友的聊天窗口
	 * 
	 * @param receive
	 */
	private void closeChatFrame(Request receive, String msg) {
		// 获取聊天窗口
		String IDs = userID + "" + receive.getSenderID();
		ChatFrame chatFrame = ManageChatFrame.getChatFrame(IDs);
		// 如果正在聊天则提示并关闭窗口
		if (chatFrame != null) {
			chatFrame.createDialog(msg);
			chatFrame.dispose();
			// 移除该聊天窗口
			ManageChatFrame.remove(IDs);
		}
	}

	/**
	 * 发送文件至好友，图片视为文件发送
	 * 
	 * @param request
	 */
	public void sendFile(Request request) {
		final FileInfo fileInfo = request.getFileInfo();

		// 读取文件
		BufferedInputStream bis = null;
		// 发送文件
		OutputStream out = null;
		// 创建套接字连接接收方的服务器，然后发送文件
		Socket socket = null;
		try {
			// 创建文件传输端口。接收文件的一方作为服务器接收文件，图片同理
			socket = new Socket(InetAddress.getLocalHost().getHostAddress(), fileInfo.getRecipientPort());
			out = socket.getOutputStream();

			// 文件或图片输入
			bis = new BufferedInputStream(new FileInputStream(fileInfo.getSrcFilePath()));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = bis.read(buffer)) != -1) {
				// 发送给接收者
				out.write(buffer, 0, len);
			}
			out.flush();
			// 通过这个方法获取!发送者!的chatFrame
			ChatFrame chatFrame = getSenderChatFrame(request);
			// 为了与接收方区分，这里换一个反馈类型
			if (request.getFeedback() == MsgType.RECEIVE_FILE_YES) {
				// 发送了文件
				MyDialog.showDialog(chatFrame, "文件发送完毕!");
				// 发送完毕提醒
				// 换一个反馈类型
				request.setFeedback(MsgType.SEND_FILE);
				// 显示
				chatFrame.setMessage(request);
			} else if (request.getFeedback() == MsgType.RECEIVE_IMAGE_YES) {
				// 发送了图片，也换一个反馈类型
				request.setFeedback(MsgType.SEND_IMAGE);
				chatFrame.setMessage(request);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 当好友发送信息时取得或者创建聊天界面，显示好友的信息<br>
	 * 处理了“我未打开聊天界面”的情况
	 * 
	 * @param receive
	 * @return 与好友聊天的ChatFrame
	 */
	private ChatFrame getFriendChatFrame(Request receive) {
		// 在收到数据后要对好友聊天界面添加信息或提示，需要用到这个方法获取聊天界面对象

		// 这地方有点乱所以搞了这么多输出一句，可以删掉
//		System.out.println("——————————————————————————————————————————————————————————————————————");
//		System.out.println("SenderID " +receive.getSenderID());
//		System.out.println("RecipientID " +receive.getRecipientID());
//		System.out.println("请求：" +receive.getRequestion());
		String IDs = receive.getRecipientID() + "" + receive.getSenderID();
//		System.out.println("ccst:getFriendChatFrame IDs="+IDs);
//		System.out.println("——————————————————————————————————————————————————————————————————————");
		/*
		 * 通过ManageChatFrame.getChatFrame(IDs)获取接收者的ChatFrame 顺序别搞错了！要获取的是 “被发送者” 与
		 * “发送者” 的聊天界面！
		 */
		ChatFrame chatFrame = ManageChatFrame.getChatFrame(IDs);
		// 判断用户有没有打卡聊天窗口
		if (chatFrame == null) {
			// 如果用户没有打开聊天窗口，则创建新的聊天窗口

			// 接收信息的用户User对象
			User receiveUser = socket.getMainFrame().getUser();
			String friendMsg = receive.getSenderNickname() + "[" + receive.getSenderID() + "]";
			// 创建窗口
			chatFrame = new ChatFrame(friendMsg, receiveUser);
			// 加入到管理ChatFrame的HashMap中
			ManageChatFrame.addChatFrame(IDs, chatFrame);
		}
		return chatFrame;
	}

	/**
	 * 当好友发送信息时取得或者创建聊天界面，显示好友的信息<br>
	 * 
	 * @param receive
	 * @return 与好友聊天的ChatFrame
	 */
	private ChatFrame getSenderChatFrame(Request receive) {
		/*
		 * 在发送文件时，如果对方还未选择是否接受文件 发送者就已经关闭了聊天窗口，需要用到这个方法获取聊天界面对象
		 */
		String IDs = receive.getSenderID() + "" + receive.getRecipientID();
		/*
		 * 通过ManageChatFrame.getChatFrame(IDs)获取接收者的ChatFrame 顺序别搞错了！要获取的是 “被发送者” 与
		 * “发送者” 的聊天界面！
		 */
		ChatFrame chatFrame = ManageChatFrame.getChatFrame(IDs);
		// 判断用户有没有打开聊天窗口
		if (chatFrame == null) {
			// 如果用户没有打开聊天窗口，则创建新的聊天窗口

			// 接收信息的用户User对象
			User senderUser = socket.getMainFrame().getUser();
			String friendMsg = senderUser.getNickname() + "[" + senderUser.getID() + "]";
			// 创建窗口
			chatFrame = new ChatFrame(friendMsg, senderUser);
			// 加入到管理ChatFrame的HashMap中
			ManageChatFrame.addChatFrame(IDs, chatFrame);
		}
		return chatFrame;
	}

	/**
	 * 我开始接收文件或者图片
	 */
	private void receiveFile(Request request) {
		final FileInfo fileInfo = request.getFileInfo();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(fileInfo.getRecipientPort());
			// 接收图片或者文件
			socket = serverSocket.accept();
			bis = new BufferedInputStream(socket.getInputStream());

			//这里通过getFileToSave方法处理了存储位置下有同名文件的情况，
			File file = SavaFileUtils.getFileToSave(fileInfo.getRecipientFilePath());
			// 改变存储路径
			fileInfo.setRecipientFilePath(file.toString());
			// 不要忘记再次添加，聊天窗口才能正确显示
			request.setFileInfo(fileInfo);

			bos = new BufferedOutputStream(new FileOutputStream(file));

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.flush();

			// 获取我与该好友聊天的界面
			ChatFrame chatFrame = getFriendChatFrame(request);

			// 判断是发送的是图片还是文件
			if (request.getRequestion() == MsgType.RECEIVE_FILE_YES) {
				// 文件接收完毕
				MyDialog.showDialog(chatFrame, "文件接收完毕!存放在[" + fileInfo.getRecipientFilePath() + "]中\n");
			}
			// 如果是图片接收完毕，不需要弹出对话框

			/*
			 * 更改Feedback 接收图片：MsgType.RECEIVE_IMAGE_OK 接收文件：MsgType.RECEIVE_FILE_YES
			 * 然后调用方法显示在消息记录区域
			 */
			request.setFeedback(request.getRequestion());
			chatFrame.setMessage(request);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (serverSocket != null && !serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * 处理方式就是和接收文件一样，直接保存到默认保存位置 不同的是不必判断是否接受
	 */
	/**
	 * 反馈给发送者信息，同时创建服务器准备接收图片
	 * 
	 * @param receive
	 */
	private void prepareReceiveImage(Request receive) {
		// 获取图片信息
		FileInfo imageInfo = receive.getFileInfo();
		String imageName = imageInfo.getFileName();
		// 检查存储位置是否可用
		SavaFileUtils.inspectFolder(defaultPath);
		// 保存在默认的路径下
		imageInfo.setRecipientFilePath(defaultPath + "/images/" + imageName);
		// 传入接收者的本地IP
		imageInfo.setRecipientIP(socket.getIp());
		// 传入接收者接收文件的端口号
		imageInfo.setRecipientPort(MySocket.RECEIVE_PORT);
		// 准备接收图片，这里要写 Requestion而不是Feedback
		receive.setRequestion(MsgType.RECEIVE_IMAGE_YES);
		// 将信息发回发送人
		sendInfoServer(receive);
		// 开始准备接收文件，通过方法实现
		receiveFile(receive);
	}

	/**
	 * 刷新好友列表
	 * 
	 * @param userID
	 */
	public void refreshFriend(Long userID) {
		try {
			Request req = new Request();
			/*
			 * SenderID本来是用在聊天的时候用于表示发送者的 此处先用来让服务器识别请求的对象了 因为再创建一个User类比较麻烦且显得累赘
			 * 也不想在Request里面再加上userID属性，因为只有这里用到 老铁们我做得对吗！（正道的光）
			 */
			req.setSenderID(userID);
			// Refresh是服务器通知客户端刷新好友列表时的类型
			req.setRequestion(MsgType.FRIEND);
			socket.oos.writeObject(req);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 选择是否接受文件
	 */
	private void chooseAcceptFile(Request receive) {
		Long sendID = receive.getSenderID();

		FileInfo fileInfo = receive.getFileInfo();
		String fileName = fileInfo.getFileName();
		// 多选项对话框
		// 选项名称
		String[] options = { "接收文件", "另存为", "取消" };
		String str = receive.getSenderNickname() + " [" + sendID + "] 向您发送文件 [" + fileName + "]\n同意接收吗?";

		// 获取接收者的窗口以弹出对话框
		// 获取或者创建聊天界面
		ChatFrame chatFrame = getFriendChatFrame(receive);
		int select = -1;
		synchronized (chatFrame) {
			select = JOptionPane.showOptionDialog(chatFrame, str, "文件传输", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, "接受文件");
		}
		switch (select) {
		case 0: {
			// 接收文件
			// 保存在用户的默认储存位置 D:/账号ID/files/
			// 检查存储位置是否可用
			SavaFileUtils.inspectFolder(defaultPath);
			fileInfo.setRecipientFilePath(defaultPath + "/files/" + fileName);
			// 传入接收者的本地IP
			fileInfo.setRecipientIP(socket.getIp());
			// 传入接收者接收文件的端口号
			fileInfo.setRecipientPort(MySocket.RECEIVE_PORT);
			
			//添加信息
			receive.setFileInfo(fileInfo);
			// 同意接收，这里要写 Requestion而不是Feedback
			receive.setRequestion(MsgType.RECEIVE_FILE_YES);
			// 将信息发回发送人
			sendInfoServer(receive);
			// 开始准备接收文件
			receiveFile(receive);
			break;
		}
		case 1: {
			// 另存为
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new File(fileName));
			int result = chooser.showSaveDialog(chatFrame);
			if (result == JFileChooser.APPROVE_OPTION) {
				// 设置目的地文件名，传入fileInfo中
				fileInfo.setRecipientFilePath(chooser.getSelectedFile().getAbsolutePath());
				// 传入接收者的本地IP
				fileInfo.setRecipientIP(socket.getIp());
				// 传入接收者接收文件的端口号
				fileInfo.setRecipientPort(MySocket.RECEIVE_PORT);

				//添加信息
				receive.setFileInfo(fileInfo);
				// 同意接收
				receive.setRequestion(MsgType.RECEIVE_FILE_YES);
				// 将信息发回发送人
				sendInfoServer(receive);
				// 开始准备接收文件
				receiveFile(receive);
			}
			break;
		}
		case 2: {
			// 等同于关闭窗口
		}
		default:
			// 取消
			System.out.println("ccst:ChooseAcceptFile:取消接收文件！");
			receive.setRequestion(MsgType.RECEIVE_FILE_NO);
			// 将信息发回发送人
			sendInfoServer(receive);
			break;
		}
	}
}
