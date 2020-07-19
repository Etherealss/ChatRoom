package client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import client.model.ManageChatFrame;
import client.model.listener.ChatFrameListener;
import client.model.util.SavaFileUtils;
import common.FileInfo;
import common.Request;
import common.User;

public class ChatFrame extends JFrame {

	/** ChatFrame */
	private static final long serialVersionUID = 5124325956186110680L;

	/** 本窗口的宽 */
	private int frameWidth = 800;
	/** 本窗口的高 */
	private int frameHeight = 800;
	/** 消息记录区域的宽 */
	private int jtpMsgWidth = 720;
	/** 消息记录区域的高 */
	private int jtpMsgHeight = 400;
	/** 发送文本按钮，发送图片按钮，发送文件按钮，聊天记录按钮 */
	private JButton jbSend, jbImage, jbFile, jbRecords;
	/** 文本输入框 */
	private JTextPane jtpChat;
	/** 消息记录区域 */
	private JTextPane jtpMsg;

	/** 带滚动条的文本输入窗口 */
	private JScrollPane jspChat;
	/** 中心面板，带滚动条的消息记录区域 */
	private JScrollPane jspMsg;
	/** 底层面板 ，放入了按钮和输入框 */
	private JPanel bottom;

	/** 顶层面板 聊天对象的昵称标签 */
	private JLabel jlUserName;
	/** 本用户的user对象 */
	private User user;

	private String friendMsg;
	private Long friendID;
	private String friendNickname;

	ChatFrameListener listener = new ChatFrameListener(this);
	/** 日期文字样式 */
	private static SimpleAttributeSet dateFont = null;
	/** 聊天用户信息的文字样式 */
	private static SimpleAttributeSet userFont = null;
	/** 用户信息文字样式 */
	private static SimpleAttributeSet msgFont = null;
	static {

		dateFont = new SimpleAttributeSet();
		StyleConstants.setForeground(dateFont, Color.GRAY);
		StyleConstants.setFontFamily(dateFont, "微软雅黑");
		StyleConstants.setFontSize(dateFont, 14);

		userFont = new SimpleAttributeSet();
		StyleConstants.setForeground(userFont, Color.BLACK);
		StyleConstants.setFontFamily(userFont, "微软雅黑");
		StyleConstants.setFontSize(userFont, 15);

		msgFont = new SimpleAttributeSet();
		StyleConstants.setForeground(msgFont, Color.BLACK);
		StyleConstants.setFontFamily(msgFont, "微软雅黑");
		StyleConstants.setFontSize(msgFont, 20);
	}

	/**
	 * 创建聊天界面
	 * 
	 * @param friendString 包含了好友昵称和好友ID
	 * @param user
	 */
	public ChatFrame(String friendString, User user) {
		this.user = user;
		friendMsg = friendString;
		this.setIconImage(new ImageIcon("src/images/chatroom.png").getImage());

		// 截取好友昵称
		int index = friendMsg.indexOf("[");
		friendNickname = friendMsg.substring(0, index);
		// 截取好友ID
		String string = friendMsg.substring(index + 1, friendMsg.length() - 1);
		friendID = Long.parseLong(string);
		System.out.println(string + "," + friendNickname);
		// 初始化界面
		init();
	}

	private void init() {
		// 设置窗口坐标,程序运行时窗口出现在屏幕中央偏左侧
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - frameWidth) / 2 + 300, (dim.height - frameHeight) / 2, frameWidth, frameHeight);

		this.setLayout(new BorderLayout());
		// 各组件设置
		// ——————————————————————————————————顶层 用户标签 ——————————————————————————————————
		jlUserName = new JLabel("   " + friendMsg, JLabel.LEFT);
		this.setTitle("你正在与" + friendMsg + "聊天");
		jlUserName.setPreferredSize(new Dimension(0, 50));
		jlUserName.setFont(new Font("微软雅黑", 0, 18));

		// ————————————————————————————————中心 消息显示区域 ——————————————————————————————————
		jtpMsg = new JTextPane();
		jtpMsg.setBounds(28, 30, jtpMsgWidth, jtpMsgHeight);
		// 设置窗口不可编辑
		jtpMsg.setEditable(false);

		// 给消息记录区域滚动条
		jspMsg = new JScrollPane(jtpMsg);
		// 设置复合边框，外出是标题边框，内层是下凹边框
		Border inner = BorderFactory.createLoweredBevelBorder();
		Border outter = BorderFactory.createTitledBorder("信息窗口");
		Border compound = BorderFactory.createCompoundBorder(outter, inner);
		jspMsg.setBorder(compound);

		// —————————————————————————————————底层 输入区域及按钮 —————————————————————————————————
		// 创建文件按钮、图片按钮、历史记录按钮、发送按钮
		jbFile = creatButton(new ImageIcon("src/images/file1.png"));
		jbFile.setActionCommand("文件");
		jbImage = creatButton(new ImageIcon("src/images/image.png"));
		jbImage.setActionCommand("图片");
		jbRecords = creatButton(new ImageIcon("src/images/records.png"));
		jbRecords.setActionCommand("历史记录");
		jbSend = creatButton(" 发  送 ");
		jbSend.setActionCommand("发送");

		// 设置发送按钮背景不透明
		jbSend.setContentAreaFilled(true);
		// 文本输入框
		jtpChat = new JTextPane();
		jtpChat.setFont(new Font("宋体", 0, 22));
		// 将所有的按钮添加在一个JPanel中
		JPanel jp_buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jp_buttons.add(jbFile);
		jp_buttons.add(jbImage);
		jp_buttons.add(jbRecords);
		jp_buttons.setBackground(Color.WHITE);

		// 模仿微信设置发送按钮
		JPanel jp_send = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		jp_send.setBackground(Color.WHITE);
		jp_send.add(jbSend);

		// 输入框添加滚动条
		jspChat = new JScrollPane(jtpChat);
		// 分别设置水平和垂直滚动条自动出现
		jspChat.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jspChat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jspChat.setPreferredSize(new Dimension(0, 150));
		// 添加空白边框素
		jspChat.setBorder(BorderFactory.createLineBorder(Color.WHITE));

		bottom = new JPanel(new BorderLayout());
		bottom.add(jp_buttons, BorderLayout.NORTH);
		bottom.add(jspChat, BorderLayout.CENTER);
		bottom.add(jp_send, BorderLayout.SOUTH);

		addComponent();
		this.setVisible(true);
		// 整个窗口可关闭时仅隐藏
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// 窗口监听器
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// 移除聊天窗口
				String IDs = user.getID() + "" + friendID;
				ManageChatFrame.remove(IDs);
				// 关闭
				dispose();
			}
		});
	}

	private void addComponent() {
		this.add(jlUserName, BorderLayout.NORTH);
		this.add(jspMsg, BorderLayout.CENTER);
		this.add(bottom, BorderLayout.SOUTH);
	}

	/**
	 * 创建按钮的方法
	 * 
	 * @param name
	 * @return a JButton
	 */
	private JButton creatButton(String name) {
		JButton button = new JButton(name);
		button.setContentAreaFilled(false);
		button.setFont(new Font("宋体", 0, 15));
		button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		button.addActionListener(listener);
		return button;
	}

	/**
	 * 创建按钮的方法
	 *
	 * @param icon
	 * @return a JButton
	 */
	private JButton creatButton(ImageIcon icon) {
		JButton button = new JButton(icon);
		// 按钮背景透明
		button.setContentAreaFilled(false);
		button.setFont(new Font("宋体", 0, 15));
		button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		button.addActionListener(listener);
		return button;
	}

	/**
	 * 判断信息以调用不同的方法来将信息显示在信息显示窗口上
	 * 
	 * @param receive
	 */
	public void setMessage(Request receive) {

		// 判断反馈类型
		switch (receive.getFeedback()) {
		case SEND_OK: {
			// 本用户发送信息成功
			sendMsgOK(receive);
			break;
		}
		case SEND: {
			// 接收到好友的信息
			sendMsg(receive);
			break;
		}
		case RECEIVE_FILE_YES: {
			// 接收文件成功
			receiveFile(receive);
			break;
		}
		case SEND_FILE: {
			// 成功给好友发送了文件
			sendFileOK(receive);
			break;
		}
		case RECEIVE_IMAGE_YES: {
			// 成功接收了图片
			receiveImage(receive);
			break;
		}
		case SEND_IMAGE: {
			sendImageOK(receive);
			// 成功给好友发送了图片
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 本用户发送信息成功，消息记录区域插入记录
	 * 
	 * @param receive
	 */
	private void sendMsgOK(Request receive) {
		// 日期格式
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		// 获得消息记录区域的Document
		StyledDocument doc = jtpMsg.getStyledDocument();
		// 自己发送的文字信息
		String content = receive.getContent() + "\n";
		String date = form.format(receive.getDate()) + "\n";
		String userMessage = user.getNickname() + "（我）说：";

		try {
			// 插入文本
			doc.insertString(doc.getLength(), date, dateFont);
			doc.insertString(doc.getLength(), userMessage, userFont);
			doc.insertString(doc.getLength(), content, msgFont);
//						JPanel jp = createBubble(receive, true);
//						 //设置光标在最后
//						jtpMsg.setCaretPosition(jtpMsg.getDocument().getLength());
//						jtpMsg.insertComponent(jp);
			// 图片后面加个换行
			doc.insertString(doc.getLength(), "\n", new SimpleAttributeSet());

		} catch (BadLocationException b) {
			b.printStackTrace();
		}
	}

	/**
	 * 信息接收成功，消息记录区域插入记录
	 * 
	 * @param receive
	 */
	private void sendMsg(Request receive) {
		// 日期格式
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		// 获得消息区域的Document
		StyledDocument doc = jtpMsg.getStyledDocument();
		// 好友发来的文字信息
		String content = receive.getContent() + "\n";
		String date = form.format(receive.getDate()) + "\n";
		String userMessage = friendNickname + " 说：";
		// 信息发送完毕
		// 获得JTextPane的Document
		try {
			// 插入文本
			doc.insertString(doc.getLength(), date, dateFont);
			doc.insertString(doc.getLength(), userMessage, userFont);
			doc.insertString(doc.getLength(), content, msgFont);

		} catch (BadLocationException b) {
			b.printStackTrace();
		}
	}

	/**
	 * 接收文件成功。消息记录区域插入记录
	 * 
	 * @param receive
	 */
	private void receiveFile(Request receive) {
		// 日期格式
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		// 获得消息区域的Document
		StyledDocument doc = jtpMsg.getStyledDocument();
		// 接收文件成功
		FileInfo fileInfo = receive.getFileInfo();

		String date = form.format(receive.getDate()) + "\n";
		String userMessage = "  " + friendNickname + " 给我发来文件：\n";
		try {
			doc.insertString(doc.getLength(), date, dateFont);
			doc.insertString(doc.getLength(), userMessage, userFont);

			/*
			 * 创建文件按钮，用按钮的形式把文件显示在消息区域
			 */
			String filePath = fileInfo.getRecipientFilePath();
			String fileName = fileInfo.getFileName();
			JButton fileBtn = SavaFileUtils.getFileButton(fileName, filePath);

			// 设置光标在最后
			jtpMsg.setCaretPosition(jtpMsg.getDocument().getLength());
			// 插入到消息区域
			jtpMsg.insertComponent(fileBtn);
			// 在消息区域的按钮之后加个换行
			doc.insertString(doc.getLength(), "\n", msgFont);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送文件成功，消息记录区域插入记录
	 * 
	 * @param receive
	 */
	private void sendFileOK(Request receive) {
		// 日期格式
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		// 获得消息记录区域的Document
		StyledDocument doc = jtpMsg.getStyledDocument();
		// 发送文件成功
		FileInfo fileInfo = receive.getFileInfo();

		String date = form.format(receive.getDate()) + "\n";
		String userMessage = user.getNickname() + "（我）发送了文件：\n\n";
		try {
			doc.insertString(doc.getLength(), date, dateFont);
			doc.insertString(doc.getLength(), userMessage, userFont);
			/*
			 * 创建文件按钮，用按钮的形式把文件显示在消息区域
			 */
			// 源文件路径（在本地）
			String filePath = fileInfo.getSrcFilePath();
			String fileName = fileInfo.getFileName();
			JButton fileButton = SavaFileUtils.getFileButton(fileName, filePath);

			// 设置光标在最后
			jtpMsg.setCaretPosition(jtpMsg.getDocument().getLength());
			// 插入到消息区域
			jtpMsg.insertComponent(fileButton);
			// 在消息区域的按钮之后加个换行
			doc.insertString(doc.getLength(), "\n", msgFont);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 成功发送图片，插入记录
	 * 
	 * @param receive
	 */
	private void sendImageOK(Request receive) {

		// 日期 + 自己的用户信息
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		String userMessage = user.getNickname() + "（我）：\n\n";
		String msg = form.format(receive.getDate()) + "\n" + userMessage;
		// 获取本地的图片路径，创建图片对象
		FileInfo imageInfo = receive.getFileInfo();
		String imagePath = imageInfo.getSrcFilePath();
		String fileName = imageInfo.getFileName();
		// 将图片发送到本地储存路径下
		SavaFileUtils.savaImage(user.getDefaultPath(), imagePath, fileName);

		// 通过方法插入图片
		insertImage(imagePath, msg);

	}

	/**
	 * 接收到好友发来的图片，插入记录
	 * 
	 * @param receive
	 */
	private void receiveImage(Request receive) {
		// 日期 + 好友的用户信息
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		String userMessage = receive.getSenderNickname() + " 说：\n\n";
		String msg = form.format(receive.getDate()) + "\n" + userMessage;
		// 获取本地的图片路径，创建图片对象
		FileInfo imageInfo = receive.getFileInfo();
		// 获取用户保存后的图片
		String imagePath = imageInfo.getRecipientFilePath();

		// 通过方法插入图片
		insertImage(imagePath, msg);
	}

	/**
	 * 将图片插入到消息显示区域
	 */
	private void insertImage(String imagePath, String msg) {
		// 创建对象并对图片的大小进行处理
		ImageIcon imageIcon = new ImageIcon(imagePath);
		ImageIcon preferImageIcon = getPreferredImageIcon(imageIcon);

		// 获得消息区域的Document
		StyledDocument doc = jtpMsg.getStyledDocument();
		try {
			// 插入日期和用户信息
			doc.insertString(doc.getLength(), msg, dateFont);

			// 设置光标在最后
			jtpMsg.setCaretPosition(jtpMsg.getDocument().getLength());
			// 插入图片
			jtpMsg.insertIcon(preferImageIcon);
			// 之后加个换行
			doc.insertString(doc.getLength(), "\n", msgFont);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对图片的大小进行调整
	 * 
	 * @param imageIcon
	 * @return
	 */
	private ImageIcon getPreferredImageIcon(ImageIcon imageIcon) {
		/*
		 * 设置图片大小 图像不能超出控制范围 图像比较按原始比例显示
		 */
		if (imageIcon != null) {
			// 获取图片的大小
			int iWidth = imageIcon.getIconWidth();
			int iHeight = imageIcon.getIconHeight();
			// 如果图片太宽，超出了显示区域的范围，则进行下面操作将图片等比例缩小
			if (jtpMsgWidth < iWidth) {
				/*
				 * 先尝试以窗口之宽度作为图片宽度 按比例绘制图片 用窗口的宽作为图片的宽fitWidth 再用图片的原宽高比得出高度fitHeigth
				 */
				int fitWidth = jtpMsgWidth;
				int fitHeigth = jtpMsgWidth * iHeight / iWidth;
				// 若图片高度fitHeight超出宽度高度，就以窗口高度为图片高度，按比例绘制图片
				if (fitHeigth > jtpMsgHeight) {
					fitHeigth = jtpMsgHeight;
					fitWidth = jtpMsgHeight * iWidth / iHeight;
				}
				// 设置图片大小，减去5px是为了美观
				imageIcon.setImage(
						imageIcon.getImage().getScaledInstance(fitWidth - 5, fitHeigth - 5, Image.SCALE_DEFAULT));
			}
		}
		return imageIcon;
	}

	/**
	 * 创建警告对话框
	 * 
	 * @param message
	 */
	public void createDialog(String message) {
		// 弹窗图标的设置
		Icon icon = new ImageIcon("src/images/exclamation.png");
		synchronized (this) {
			JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.PLAIN_MESSAGE, icon);
		}
	}

	/**
	 * @return 返回聊天输入框
	 */
	public JTextPane getJtpChat() {
		return jtpChat;
	}

	/**
	 * @return 返回消息显示区域
	 */
	public JTextPane getJtpMsg() {
		return jtpMsg;
	}

	public String getFriendNickname() {
		return friendNickname;
	}

	/**
	 * @return friendID
	 */
	public Long getFriendID() {
		return friendID;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return user;
	}

	// 常用
	/**
	 * @return 当前用户ID
	 */
	public long getUserID() {
		return user.getID();
	}

	public ChatFrame() {
	}
}
