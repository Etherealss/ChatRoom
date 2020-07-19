package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import client.MySocket;

public class MyServer extends ServerSocket{

	
	public MyServer() throws IOException{}
	/**
	 * 创建服务器
	 * @param port
	 * @throws IOException
	 */
	public MyServer(int port) throws IOException {
		super(port);
	}

	/**
	 * @return MySocket
	 */
	@Override
	public MySocket accept() throws IOException  
    {
        MySocket s = new MySocket();
        // 将accept方法返回的对象类型设为MySocket
        implAccept(s);  
        return s;
    }

}
