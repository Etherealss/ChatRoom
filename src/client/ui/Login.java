package client.ui;

import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import client.model.ClientConnectServer;
import client.ui.util.ModelFrame;
import client.ui.util.MyDialog;
import common.MsgType;
import common.Request;
import common.User;

/**
 * 登录类
 * 
 * @author 寒洲
 * @date:May 20, 2020
 */
public class Login {

	/** 本窗口 */
	private ModelFrame login;
	/** 按钮 */
	private JButton jbLogin, jbRegister;
	/** 用户名输入框 */
	private JTextField jtfUserID;
	/** 密码输入框 */
	private JPasswordField jpfPassword;
	/** 文字标签 */
	private JLabel jlUserID, jlPassword;

	public Login() {
		System.out.println("启动客户端登录界面...");
		// 创建窗口
		// 将窗口大小传入模型窗口类ModelFrame，此处设置400*300
		login = new ModelFrame("登录", 400, 300);
		login.setIconImage(new ImageIcon("src/images/login.png").getImage());
		login.setLayout(null);
		// 加入账号标签
		jlUserID = new JLabel("账 号");
		// 设置位置并添加，下面不多做注释
		jlUserID.setBounds(60, 70, 60, 28);
		jlUserID.setFont(new Font("宋体", 0, 15));
		login.add(jlUserID);

		// 加入密码标签
		jlPassword = new JLabel("密 码");
		jlPassword.setBounds(60, 122, 60, 28);
		jlPassword.setFont(new Font("宋体", 0, 15));
		login.add(jlPassword);

		// 添加账号文本输入框，在此输入账号
		jtfUserID = login.getJtfUserID();
		jtfUserID.setBounds(140, 68, 161, 28);
		login.add(jtfUserID);

		// 密码文本输入框
		jpfPassword = login.getJpfPassword();
		jpfPassword.setBounds(140, 120, 161, 25);
		// 把输入在密码框中的文本替换为"*"显示
		jpfPassword.setEchoChar('*');
		login.add(jpfPassword);

		// 按钮—登录
		jbLogin = new JButton("登       录");
		jbLogin.setBounds(80, 170, 210, 23);
		login.add(jbLogin);

		// 按钮-注册
		jbRegister = new JButton("注       册");
		jbRegister.setBounds(80, 200, 210, 23);
		login.add(jbRegister);

		// 添加登录监听器
		jbLogin.addActionListener(e -> {
			// 检查ID和密码：
			if (login.inspectUserID()) {
				if (login.inspectPassword()) {
					// 账号密码没有问题
					// 获取用户名和密码
					Long userIDText = Long.parseLong(jtfUserID.getText());
					String password = String.valueOf(jpfPassword.getPassword());
					// 通过User类传递用户信息
					User user = new User(userIDText, password);
					// 发送给服务器验证账号，获取服务器反馈的结果
					ClientConnectServer ccs = new ClientConnectServer();
					try {
						// 取得结果
						Request feedback = ccs.loginInfoServer(user);
						MsgType message = feedback.getFeedback();
						if (message == MsgType.LOGIN_YES) {
							// 登录成功提示框
							// 关闭登录窗口，打开主窗口
							login.dispose();
							MainFrame mainFrame = new MainFrame(feedback.getUser());
							MyDialog.showDialog(mainFrame, "恭喜这位小可爱< " + feedback.getUser().getNickname() + " 登录成功");

						} else if (message == MsgType.USER_NOT_FOUND) {
							// 用户不存在，提示是否注册
							registerReminder();
						} else if (message == MsgType.LOGIN_WRONG) {
							// createDialog是ModelFrame中的创建对话框的方法
							login.createDialog("你丫密码错了！");
						} else if (message == MsgType.LOGINED) {
							login.createDialog("该账号已在别处登录！");
						}
					} catch (Exception e1) {
						login.createDialog("连接服务器失败！请检查你的网络状态！");
						e1.printStackTrace();
					}
				}
			}
		});

		// 添加注册监听器
		jbRegister.addActionListener(e -> {
			// 关闭登录窗口
			login.dispose();
			// 打开注册窗口
			new Register();
		});

	}

	/**
	 * 注册提醒对话框
	 */
	private void registerReminder() {
		// 用以设置图标
		Icon icon = new ImageIcon("src/images/question.png");
		// 设置对话框的基本信息，后面也一样。这里设置了弹窗信息、弹窗按钮标题、选项、弹窗类型，图标
		int select = JOptionPane.showConfirmDialog(null, "账号未注册！是否前往注册界面？", "错误", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE, icon);
		// select 为用户点的第几个按钮
		if (select == 0) {
			// 点了确认按钮
			// 关闭注册窗口
			login.dispose();
			// 打开注册窗口
			new Register();
		}
	}
}
