package client.ui;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import client.model.ClientConnectServer;
import client.ui.util.ModelFrame;
import common.MsgType;
import common.User;

/**
 * 注册类
 * 
 * @author 寒洲
 * @date:May 20, 2020
 */
public class Register implements ActionListener {

	/** 布局面板 */
	private JPanel jpLeft, jpCenter, jpRight, jpButtom;
	/** 确定注册按钮 */
	private JButton jbRegister;
	/** 设置头像按钮 */
	private JButton jbSetAvatar;
	/** 昵称、用户ID输入框 */
	private JTextField jtfNickName, jtfUserID;
	/** 密码文本框 */
	private JPasswordField jpfPassword, jpfPassword2;
	/** 头像标签 */
	private JLabel jlAvatar;
	/** 用户头像图片路径 */
	private String imagePath;
	/** 显示用户头像的容器 */
	private AvatarPanel avatarPanel;
	/**
	 * 性别，<br>
	 * f 代表小哥哥（默认），m 代表小姐姐
	 */
	private String sex = "f";
	/** 注册窗口 */
	private ModelFrame register;

	public Register() {
		// 创建窗口，大小为400*300px
		register = new ModelFrame("注册", 550, 300);
		register.setLayout(new BorderLayout());
		register.setIconImage(new ImageIcon("src/images/register.png").getImage());
		// —————————————————————————————— 左侧jp_left ————————————————————————————————

		jpLeft = new JPanel(new GridLayout(5, 1, 5, 5));
		jpLeft.setPreferredSize(new Dimension(100, 0));

		// 通过方法创建 并添加标签
		creatJLabel("昵      称：");
		creatJLabel("账      号：");
		creatJLabel("密      码：");
		creatJLabel("再次输入：");
		creatJLabel("性      别：");

		register.add(jpLeft, BorderLayout.WEST);

		// —————————————————————————————— 中间jp_center ——————————————————————————————

		jpCenter = new JPanel(new GridLayout(5, 1, 5, 5));
		jpCenter.setPreferredSize(new Dimension(300, 0));
		// 昵称
		jtfNickName = new JTextField();
		jpCenter.add(jtfNickName);

		// 账号文本输入框
		jtfUserID = register.getJtfUserID();
		jpCenter.add(jtfUserID);

		// 密码文本输入框
		jpfPassword = register.getJpfPassword();
		// 把输入在密码框中的文本替换为"*"显示
		jpfPassword.setEchoChar('*');
		jpCenter.add(jpfPassword);

		// 第二次输入密码的文本框
		jpfPassword2 = new JPasswordField();
		// 把输入在密码框中的文本替换为"*"显示
		jpfPassword2.setEchoChar('*');
		jpCenter.add(jpfPassword2);

		// 性别单选框
		ButtonGroup sexGroup = new ButtonGroup();
		JRadioButton male = createRadioButton("男", true);
		JRadioButton female = createRadioButton("女", false);
		sexGroup.add(male);
		sexGroup.add(female);
		JPanel jp_sex = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp_sex.add(male);
		jp_sex.add(female);
		jpCenter.add(jp_sex);

		register.add(jpCenter, BorderLayout.CENTER);

		// —————————————————————————————— 右侧jp_right ———————————————————————————————
		// 设置用户头像的区域
		jpRight = new JPanel(null);
		jpRight.setPreferredSize(new Dimension(150, 0));
		jpRight.setBorder(new EmptyBorder(10, 10, 20, 15));
		// 用户头像标签
		jlAvatar = new JLabel("用户头像");
		jlAvatar.setBounds(47, 120, 70, 20);
		jpRight.add(jlAvatar);
		// 默认头像
		String defaultImagePath = "src/images/avatars/boy.png";
		avatarPanel = new AvatarPanel(defaultImagePath);
		avatarPanel.setBounds(20, 20, 100, 100);
		jpRight.add(avatarPanel);

		// 选择图片的按钮
		jbSetAvatar = new JButton("浏览");
		jbSetAvatar.setActionCommand("浏览");
		jbSetAvatar.addActionListener(this);
		jbSetAvatar.setBounds(37, 150, 70, 20);
//		jpRight.add(jbSetAvatar, BorderLayout.SOUTH);

		jpRight.add(jbSetAvatar);

		register.add(jpRight, BorderLayout.EAST);

		// ——————————————————————————————— 底部按钮 ————————————————————————————————————
		// 按钮-注册
		jbRegister = new JButton("注     册");
		jbRegister.setActionCommand("注册");
		// 注册监听器
		jbRegister.addActionListener(this);
		// 添加按钮
		jpButtom = new JPanel();
		jpButtom.setLayout(new FlowLayout(FlowLayout.CENTER));
		jpButtom.setOpaque(false);
		jpButtom.add(jbRegister);
		// 居中
		jpButtom.setPreferredSize(new Dimension(0, 40));
		register.add(jpButtom, BorderLayout.SOUTH);

		// 为了让界面更好看，我在上面加了个空白的JPanel
		JPanel jp_top = new JPanel();
		jp_top.setPreferredSize(new Dimension(0, 10));
		register.add(jp_top, BorderLayout.NORTH);

		// ——————————————————————————————— 关闭窗口 ————————————————————————————————————
		register.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		register.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("关闭注册窗口");
				register.dispose();
				// 打开登录界面
				new Login();
			}
		});
	}

	/**
	 * 创建文字标签，并添加到jpLeft中
	 * 
	 * @param 标签文字内容
	 */
	private void creatJLabel(String title) {
		JLabel jl = new JLabel(title, JLabel.RIGHT);
		jl.setFont(new Font("微软雅黑", 0, 14));
		jpLeft.add(jl);
	}

	/**
	 * 创建单选框按钮，用于选择性别
	 * 
	 * @param sex
	 * @param bool
	 * @return JRadioButton
	 */
	private JRadioButton createRadioButton(String sex, Boolean bool) {
		JRadioButton jrb = new JRadioButton(sex, bool);
		jrb.setActionCommand(sex);
		jrb.addActionListener(this);
		return jrb;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("男".equals(e.getActionCommand())) {
			sex = "f";
		} else if (e.getActionCommand().equals("女")) {
			sex = "m";
		} else if (e.getActionCommand().equals("注册")) {

			// 获取登录名和密码
			String userIDText = jtfUserID.getText();
			String password = String.valueOf(jpfPassword.getPassword());
			String rePasswordText = String.valueOf(jpfPassword2.getPassword());

			// 检查账号密码的输入问题
			if (register.inspectUserID()) {
				if (register.inspectPassword()) {
					// 账号密码莫得问题
					if (!password.equals(rePasswordText)) {
						// 两次密码不同
						register.createDialog("输入的密码不相同！");
					} else if (rePasswordText.equals("")) {
						// 没有第二次输入密码
						register.createDialog("请再次输入密码！");
					} else {
						// 检查完毕，发送信息至服务器验证账户
						Long userID = Long.parseLong(userIDText);
						User user = new User(userID, password);
						user.setSex(sex);
						user.setNickname(jtfNickName.getText());
						/*
						 * 功能尚不完善，不支持自定义头像。因为头像的裁剪、缩放等功能还不能处理好
						 */
						if (sex == "f") {
							user.setAvatarPath("src/images/avatars/boy.png");
						} else {
							user.setAvatarPath("src/images/avatars/girl.png");
						}

						MsgType result = new ClientConnectServer().registerInfoServer(user);
						if (result == MsgType.REGISTER_YES) {

							register.createDialog("right.png", "恭喜这位新小可爱注册成功！即将返回登录界面！", "提示");
							register.dispose();
							new Login();

						} else if (result == MsgType.REGISTER_ID_EXIST) {

							register.createDialog("用户ID已存在！请重新输入哦");

						} else if (result == MsgType.REGISTER_FAIL) {
							//这个判断条件可删除，正常情况下不会出现的
							register.createDialog("注册失败！");

						}
					}
				}
			}
		} else if (e.getActionCommand().equals("浏览")) {
			selectAvatar();
			// TODO 进一步处理
		}
	}

	/**
	 * 选择头像
	 */
	private void selectAvatar() {

		register.createDialog("注意！功能尚不完善！选择图片后头像仍旧为默认头像！");
		// 文件名后缀过滤器
		FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("图片文件", "jpg", "jpeg", "png", "gif");
		// 图片后缀的正则表达式
		String pattern = ".+(.jpeg|.jpg|.png|.gif)$";
		// 待会儿用这个引用得到桌面路径
		FileSystemView fsv = FileSystemView.getFileSystemView();
		// 文件选择对话框
		JFileChooser chooser = new JFileChooser();
		System.out.println("Register：选择用户头像的按钮按下");
		// 设置过滤器
		chooser.setFileFilter(imageFilter);
		// 设置桌面为默认路径
		chooser.setCurrentDirectory(fsv.getHomeDirectory());
		// 显示对话框
		int ret = chooser.showOpenDialog(register);
		// 获取用户选择的结果
		if (ret == JFileChooser.APPROVE_OPTION) {
			// 选择一张图片作为头像
			// 没有做裁剪功能
			imagePath = chooser.getSelectedFile().getAbsolutePath();
			if (imagePath.matches(pattern)) {
				/*
				 * 添加到界面中 先移出原有的avatarPanel 在创建新的Panel，添加到其中 然后刷新
				 */
				System.out.print("移除avatarPanel ");
				jpRight.remove(avatarPanel);
				jpRight.repaint();

				avatarPanel = new AvatarPanel(imagePath);
				System.out.println("添加avatarPanel");
				avatarPanel.setBounds(20, 20, 100, 100);
				jpRight.add(avatarPanel);
				jpRight.validate();

			} else {
				// 不支持的图片格式
				register.createDialog("暂不支持该图片格式！");
				imagePath = null;
			}
		}
	}
}
