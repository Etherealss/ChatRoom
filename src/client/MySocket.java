package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import client.ui.MainFrame;

public class MySocket extends Socket {

	/** 当前客户端连接到服务器的输出流 */
	public ObjectOutputStream oos;
	/** 当前客户端连接到服务器的输入流 */
	public ObjectInputStream ois;
	/** 套接字对于的主窗口 */
	private MainFrame mainFrame = null;
	/** 用来接收文件的端口 */
    public static final int RECEIVE_PORT = 4444;
	public MySocket() {}

	/**
	 * 创建Socket
	 * 
	 * @param ip
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ConnectException
	 */
	public MySocket(String ip, int port) throws UnknownHostException, IOException, ConnectException{
		super(ip, port);
	}

	/**
	 * 关闭该socket下的所有流
	 */
	public void closeAll() {
		try {
			this.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			if(oos!=null) {
				this.oos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(ois!=null) {
				this.ois.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return 主界面/好友列表
	 */
	public MainFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * @param 主界面/好友列表
	 */
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	/** 本客户端的IP地址 */
	private String ip;
	/**
	 * @return 本客户端的本地IP地址
	 */
	public String getIp() {
		return ip;
	}
}
