package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.util.List;

import javax.swing.UIManager;

import client.MySocket;
import common.Request;
import common.User;
import common.MsgType;
import server.model.ManageServerConnectThread;
import server.model.ServerConnectMySQL;
import server.model.ServerConnectThread;
import server.model.util.JDBCUtils;
import server.ui.MyServerFrame;

/**
 * 启动服务器，持续监听新登录的用户的信息
 * 
 * @author 寒洲
 * @date:2020年5月27日
 */
public class LaunchServer {

	/** 服务器端口号 */
	public static int PORT;

	/** 服务器对象 */
	private static MyServer server = null;
	
	/** 服务器界面 */
	static MyServerFrame serverFrame = null;

	public static void main(String[] args) {
		System.out.println("启动服务器...");
		// 创建服务器界面
		createFrame();
		// 连接服务器的客户端
		MySocket socket = null;
		
		ObjectInputStream ois = null;
		ObjectOutputStream oosFeedback = null;
		Request feedback = null;
		
		ServerConnectThread sct = null;
		
		try {
			//连接数据库
			Connection conn = JDBCUtils.getConnection();
			PORT = JDBCUtils.getPort();
			server = new MyServer(PORT);
			
			//与数据库的连接
			ServerConnectMySQL scm = new ServerConnectMySQL(conn);
			
			//添加已注册用户信息
			List<User> registedUser = scm.getRegistedUser();
			serverFrame.setRegisteredUser(registedUser);
			
			// 监听
			while (true) {
				// 获取从客户端接收到的用户对象
				socket = server.accept();
				//获取输入流
				ois = new ObjectInputStream(socket.getInputStream());
				//信息包，接收客户端发来的请求
				Request req = (Request) ois.readObject();
				
				// 判断登录条件
				if (req.getRequestion() == MsgType.LOGIN) {
					// 接收到登录请求
					// 为该用户分配服务器输出流
					oosFeedback = new ObjectOutputStream(socket.getOutputStream());

					User user = req.getUser();
					//判断是否已经登录
					if(ManageServerConnectThread.getClientThread(user.getID()) == null) {
						//判断用户信息，同时将用户的详细信息添加到feedback的user中返回
						feedback = scm.verifyLogin(user);
						
						if (feedback.getFeedback() == MsgType.LOGIN_YES) {
							// 登录成功
							System.out.println("LaunchServer:恭喜这位小可爱："+user.getID()+"登录成功");
							//添加到服务器界面的在线用户列表中
							serverFrame.setOnlineUser(user);
							
							// 返回信息
							feedback.setFeedback(MsgType.LOGIN_YES);
							// 开启线程保持与客户端的连接
							sct = new ServerConnectThread(ois,oosFeedback,scm,serverFrame);
							//将连接的线程储存在HashMap中
							ManageServerConnectThread.addClientThread(user.getID(), sct);
							// 向登录用户的好友广播，刷新在线人员的好友列表
							Request feedback2 = new Request();
							//传入登录的用户ID，待会儿用它来好去用户好友并发送广播
							feedback2.setSenderID(user.getID());
							//提醒用户好友刷新好友列表
							feedback2.setFeedback(MsgType.REFRESH);
							
							// 好友登录提示
							String msg = "你的好友： "+feedback.getUser().getNickname()+
									"[" +feedback.getUser().getID().toString()+ "] 上线了";
							feedback2.setContent(msg);
							
							//向好友中的在线用户广播
							sct.broadcastToFriend(feedback2);
							sct.start();
							
						}
					}else {
						//设置为已经登录
						feedback.setFeedback(MsgType.LOGINED);
					}
				} else if(req.getRequestion() == MsgType.REGISTER) {
					/*
					 * 接收到注册请求
					 * 为该用户分配服务器输出流
					 */
					oosFeedback = new ObjectOutputStream(socket.getOutputStream());
					
					User user = req.getUser();
					//判断
					feedback = scm.registerNewUser(user);
					if(feedback.getFeedback() == MsgType.REGISTER_YES) {
						//注册成功，添加到已注册用户列表
						serverFrame.setRegisteredUser(user);
					}
				}
				//返回结果
				oosFeedback.writeObject(feedback);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//若没有成功登陆则关闭流。只有登陆后的流是需要用到的
			if(feedback.getFeedback() != MsgType.LOGIN_YES) {
				if(ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(oosFeedback != null) {
					try {
						oosFeedback.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 创建服务器界面
	 */
	public static void createFrame() {
		// 设置界面样式
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		serverFrame = new MyServerFrame();
		System.out.println("创建界面完毕");
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//			}
//		});
	}
}
