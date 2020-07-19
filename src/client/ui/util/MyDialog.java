package client.ui.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MyDialog {

	/**
	 * 显示一个自定义的警告对话框，7秒后自动关闭
	 * 
	 * @param owner依赖的窗口
	 * @param msg提示信息
	 */
	public static void showDialog(JFrame owner, String msg) {
		// 创建一个不阻塞的非模态对话框
		JDialog dialog = new JDialog(owner, "提示", false);
		// 设置对话框的宽高
		dialog.setSize(280, 200);
		// 设置对话框大小不可改变
		dialog.setResizable(false);
		// 设置对话框显示的位置
		// 设置窗口坐标,程序运行时窗口出现在屏幕中央
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setBounds(dim.width - dialog.getWidth()-50, dim.height - dialog.getHeight()-100, dialog.getWidth(), dialog.getHeight());
		
		Image image = new ImageIcon("src/images/exclamation.png").getImage();
		dialog.setIconImage(image);

		// 创建一个标签显示消息内容
		// 加上<html>自动换行
		JLabel messageLabel = new JLabel("<html>" + msg);
		messageLabel.setBounds(10, 10, 260, 50);
		messageLabel.setFont(new Font("宋体", 0, 15));

		// 创建一个按钮用于关闭对话框
		JButton jbOk = new JButton("确定");
		jbOk.setBounds(105, 120, 65, 30);
		jbOk.addActionListener(e -> {
			// 关闭对话框
			dialog.dispose();
		});

		// 添加内容面板
		JPanel contenPanel = new JPanel();
		contenPanel.setLayout(null);
		// 添加组件到面板
		contenPanel.add(messageLabel);
		contenPanel.add(jbOk);
		// 设置对话框的内容面板
		dialog.setContentPane(contenPanel);

		// 该对话框10秒后自动关闭
		new Timer().schedule(new TimerTask() {
			// 在run方法中的语句就是定时任务执行时运行的语句。
			public void run() {
				dialog.dispose();
			}
			// 表示在10秒之后开始执行
		}, 10000);

		// 显示对话框
		dialog.setVisible(true);
	}

}
