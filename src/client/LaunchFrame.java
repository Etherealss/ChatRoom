package client;

import java.io.IOException;

import javax.swing.UIManager;

import client.ui.*;
import client.ui.util.MyDialog;
/**
 * 启动程序的类 
 * 
 * @author 寒洲
 * @date:2020年4月3日
 */
public class LaunchFrame {

	private static void createFrame() throws IOException {
		// 窗口默认为登录窗口
		new Login();
	}

	public static void main(String[] args) {
		// 设置界面样式 
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					createFrame();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
