package client.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import client.MySocket;
import common.Request;
import common.User;
import common.MsgType;

/**
 * 启动一个客户端,连接到服务器<br>
 * 登录和注册时需要使用到。成功登录后通过线程保持与服务器的连接
 * 
 * @author 寒洲
 * @date:2020年5月26日
 */
public class ClientConnectServer {

	/** 客户端套接字 */
	private MySocket socket = null;
	/** 服务器端口号 */
	public static final int PORT = 8888;

	/**
	 * 连接服务器，请求注册新用户
	 * 
	 * @param user
	 * @return MsgType
	 */
	public MsgType registerInfoServer(User user) {
		System.out.println("ClientConnectServer: <" + user.getID() + ">请求注册...");
		String ip = null;
		// feedback用于接收服务器反馈回来的信息，获取注册情况
		Request feedback = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
			// 创建Socket
			socket = new MySocket(ip, PORT);
			// 发送登录的用户对象
			Request req = new Request();
			req.setUser(user);
			req.setRequestion(MsgType.REGISTER);
			socket.oos = new ObjectOutputStream(socket.getOutputStream());
			socket.oos.writeObject(req);

			// 接收服务器反馈
			socket.ois = new ObjectInputStream(socket.getInputStream());
			feedback = (Request) socket.ois.readObject();

			// return 反馈信息 如果反馈为REGISTER_YES则注册成功，
//			if(feedback.getFeedback() == MsgType.REGISTER_YES) {
//				System.out.println("ClientConnectServer: <"+user.getID()+">注册成功！");
//			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			socket.closeAll();
		}

		return feedback.getFeedback();
	}

	/**
	 * 连接服务器，请求登录。<br>
	 * Socket及其流在此创建<br>
	 * 
	 * @param user
	 * @return Request
	 * @throws Exception
	 */
	public Request loginInfoServer(User user) throws Exception {
		//固定为本地IP
		String ip =InetAddress.getLocalHost().getHostAddress();
		// 发送登录请求的信息包Request
		Request req = new Request();
		// feedback用于接收服务器反馈回来的信息，获取登录情况
		Request feedback = null;
		// 创建Socket
		socket = new MySocket(ip, PORT);
		
		//登录的账号
		req.setUser(user);
		//信息类型为：登录
		req.setRequestion(MsgType.LOGIN);
		//创建输出流并发送
		socket.oos = new ObjectOutputStream(socket.getOutputStream());
		socket.oos.writeObject(req);

		// 创建输入流并接收服务器的反馈
		socket.ois = new ObjectInputStream(socket.getInputStream());
		feedback = (Request) socket.ois.readObject();

		// 如果反馈为LOGIN_YES则登录成功
		if (feedback.getFeedback() == MsgType.LOGIN_YES) {
			System.out.println(user.getID() + ">登录成功！创建线程");
			/*
			 * 创建该客户端与服务器连接的线程 
			 * 使用线程可以实现多登录 
			 * 在线程中保持与服务器得了连接，持续接收服务器的信息
			 * socket中包含了IO流，一并交由ccst类控制
			 */
			ClientConnectServerThread ccst = new ClientConnectServerThread(socket);
			/*
			 * ManageClientConnectThread用户管理在线用户
			 * 其中使用了额哈希表储存线程，用用户ID作为key
			 */
			ManageClientConnectThread.setClientConServerThread(user.getID(), ccst);
			ccst.start();
		}
		return feedback;
	}
}
