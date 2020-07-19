package client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.model.ClientConnectServerThread;
import client.model.ManageChatFrame;
import client.model.ManageClientConnectThread;
import common.MsgType;
import common.Request;
import common.User;

/**
 * 聊天窗口界面
 * 
 * @author 寒洲 2020年5月31日 寒洲
 */
public class MainFrame extends JFrame implements ActionListener, MouseListener {
	/**  */
	private static final long serialVersionUID = 5815620882387936537L;
	/** 本窗口的宽 */
	public static final int F_WIDTH = 370;
	/** 本窗口的高 */
	public static final int F_HEIGTH = 800;
	/** 选项面板的宽 */
	public static final int O_WIDTH = 70;
	/** 设置面板 */
	private JPanel jpOption;
	/** 好友列表面板 */
	private FriendPanel friendPanel;

	/** 按钮 */
	private JButton jbUser, jbSetting, jbAddFriend, jbRefresh, jbRemove;

	/** 当前用户的昵称 */
	private String MyNickname;
	private Long userID;

	/** 与服务器的连接线程对象 */
	private ClientConnectServerThread ccst = null;
	/** 当前主窗口的User对象 */
	private User user = null;

	/**
	 * 启动聊天室主界面
	 * 
	 * @param user
	 */
	public MainFrame(User user) {
		this.user = user;
		MyNickname = user.getNickname();
		userID = user.getID();
		// 与客户端的连接线程，用于发送消息
		ccst = ManageClientConnectThread.getClientConServerThread(userID);
		// 将用户的默认存储位置传入线程，用于接收数据时保存
		ccst.setting(user);
		// 窗口初始化
		this.init();
	}

	/**
	 * 主界面初始化
	 */
	private void init() {
		/*
		 * 将主窗口对象添加到MySocket的属性中 ， 这里是为了在获取好友列表的时候 与服务器连接的线程可以获取mainFrame以设置其中的好友列表面板
		 */
		ccst.getSocket().setMainFrame(this);

		// 设置窗口名
		setTitle(MyNickname + " 的聊天室");
		// 设置窗口坐标,程序运行时窗口出现在屏幕中央偏左侧
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - F_WIDTH) / 2 - 300, (dim.height - F_HEIGTH) / 2, F_WIDTH, F_HEIGTH);
		// 窗口大小不可以改变
		setResizable(false);
		// 设置本程序图标
		this.setIconImage(new ImageIcon("src/images/chatroom.png").getImage());
		this.setLayout(new BorderLayout());

		// —————————————————————————————————— 选项面板 ——————————————————————————————————
		jpOption = new JPanel();
		jpOption.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 20));
		// 基本外观设置
		jpOption.setPreferredSize(new Dimension(O_WIDTH, 0));
		jpOption.setBackground(new Color(54, 54, 54));

		// 设置按钮
		jbAddFriend = createButton("addFriend.png");
		jbAddFriend.setActionCommand("添加好友");

		jbSetting = createButton("setting.png");
		jbSetting.setActionCommand("聊天室设置");

		jbRefresh = createButton("refresh.png");
		jbRefresh.setActionCommand("刷新好友列表");

		jbRemove = createButton("remove.png");
		jbRemove.setActionCommand("删除好友");

		// 以用户头像作为按钮，特殊处理
		ImageIcon icon = new ImageIcon(user.getAvatarPath());
		jbUser = new JButton(icon);
		jbUser.setFont(new Font("宋体", Font.PLAIN, 15));
		jbUser.addActionListener(this);
		jbUser.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		jbUser.setActionCommand("用户设置");

		// 按钮添加
		jpOption.add(jbUser);
		jpOption.add(jbRefresh);
		jpOption.add(jbAddFriend);
		jpOption.add(jbRemove);
		jpOption.add(jbSetting);
		// ——————————————————————————————————好友列表面板——————————————————————————————————
		// 借助通讯线程，从服务器获取好友
		// 请求服务器发送好友列表回来，通过这个类的setFriendPanel方法创建好友列表
		ccst.refreshFriend(userID);

		// 添加面板
		this.add(jpOption, BorderLayout.WEST);

		// 整个窗口可关闭
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// 关闭窗口监听器
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				logout();
			}
		});

		// 显示整个窗口
		this.setVisible(true);
	}

	/**
	 * 退出时的事件处理
	 */
	private void logout() {
		// 设置弹窗的图标
		Icon icon = new ImageIcon("src/images/question.png");
		int select = -1;
		// 设置对话框的基本信息
		synchronized (this) {
			select = JOptionPane.showConfirmDialog(null, "是否退出？", "退出", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, icon);
		}
		if (select == JOptionPane.OK_OPTION) {
			// 给服务器发送退出消息
			Request request = new Request();
			request.setRequestion(MsgType.LOGOUT);
			request.setSenderID(userID);
			// 昵称将被用于通知好友下线时的提示
			request.setSenderNickname(MyNickname);
			ccst.sendInfoServer(request);
		}
	}

	/**
	 * 刷新好友列表
	 * 
	 * @param friends
	 */
	public void setFriendPanel(List<User> friends) {
		System.out.println("MianFrame:刷新了好友列表");
		// 添加面板
		if (friendPanel != null) {
			this.remove(friendPanel);
			this.repaint();
		}
		friendPanel = new FriendPanel(friends);
		// 设置大小
		friendPanel.setPreferredSize(new Dimension(friendPanel.getWidth(), 0));
		// 为在线好友的JPanel监听器
		JLabel[] jl_friends = friendPanel.getJl_friends();
		for (int i = 0; i < jl_friends.length; i++) {
			if (jl_friends[i].isEnabled()) {
				jl_friends[i].addMouseListener(this);
			}
		}
		// 添加并刷新
		this.add(friendPanel, BorderLayout.CENTER);
		this.validate();
	}

	/**
	 * 创建按钮的方法
	 *
	 * @param icon
	 * @return a JButton
	 */
	private JButton createButton(String iconName) {
		ImageIcon icon = new ImageIcon("src/images/" + iconName);
		JButton button = new JButton(icon);
		button.setFont(new Font("宋体", Font.PLAIN, 15));
		button.addActionListener(this);
		button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return button;
	}

	// 按钮监听
	@Override
	public void actionPerformed(ActionEvent e) {
		// 判断是哪一个按钮
		switch (e.getActionCommand()) {
		case "用户设置": {
			// TODO 未完善
			System.out.println("选项面板“用户设置”按钮被按下");
			break;
		}
		case "添加好友": {
			addFriend();
			break;
		}
		case "刷新好友列表": {
			ccst.refreshFriend(userID);
			break;
		}
		case "删除好友": {
			removeFriend();
			break;
		}
		case "聊天室设置": {
			// TODO 未完善
			System.out.println("选项面板“聊天室设置”按钮被按下");
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 添加好友功能
	 */
	private void addFriend() {
		// 写总结的时候发现这里会阻塞，应该用再做一个可输入的自定义对话框 蓝瘦 没时间了
		String result = null;
		synchronized (this) {
			result = (String) JOptionPane.showInputDialog(this, "请输入你要添加的好友ID", "添加好友", JOptionPane.PLAIN_MESSAGE, null,
					null, null);
		}
		if (result != null) {
			if (inspectUserID(result)) {
				Long targetID = Long.parseLong(result);

				// 判断该ID是否可以添加
				if (targetID.equals(userID)) {
					createDialog("你想添加自己为好友？？做梦呢");
					jbAddFriend.doClick();
				} else {
					// 判断是否是好友
					if (!inspectFriend(targetID)) {
						// 不是好友，可以添加，发送请求
						Request req = new Request();
						req.setSenderID(userID);
						// 用于提示信息：
						req.setSenderNickname(MyNickname);
						// 用接收者ID储存要添加的好友的ID
						req.setRecipientID(targetID);
						req.setRequestion(MsgType.ADD_FRIEND);
						ccst.sendInfoServer(req);
					} else {
						createDialog("你们已经是好友！不可以重复添加");
						jbAddFriend.doClick();
					}
				}
			} else {
				// 输入有问题，再次点击按钮触发事件
				jbAddFriend.doClick();
			}
		}
	}

	/**
	 * 删除好友功能
	 */
	private void removeFriend() {
		String result = (String) JOptionPane.showInputDialog(this, "请输入你要删除的好友ID", "添加好友", JOptionPane.PLAIN_MESSAGE,
				null, null, null);
		if (result != null) {
			if (inspectUserID(result)) {
				Long targetID = Long.parseLong(result);

				// 判断该ID是否可以删除
				if (targetID.equals(userID)) {
					createDialog("你想删除自己？？做梦呢");
					jbRemove.doClick();
				} else {
					// 判断是否是好友
					if (inspectFriend(targetID)) {
						// 是好友，可以删除，发送请求
						Request req = new Request();
						req.setSenderID(userID);
						req.setSenderNickname(MyNickname);
						// 用接收者ID储存要删除的好友的ID
						req.setRecipientID(targetID);
						req.setRequestion(MsgType.REMOVE);
						ccst.sendInfoServer(req);
					} else {
						createDialog("你们不是好友！不可以删除");
						jbRemove.doClick();
					}
				}
			} else {
				// 输入有问题，再次点击按钮触发事件
				jbRemove.doClick();
			}
		}
	}

	/**
	 * 判断该ID是否是好友
	 * 
	 * @param targetID
	 * @return true表示是好友
	 */
	private Boolean inspectFriend(Long targetID) {
		// 获取好友列表 判断目标是否已经是好友
		JLabel[] jl_friends = friendPanel.getJl_friends();
		for (int i = 0; i < jl_friends.length; i++) {
			// 截取好友ID
			String str_ID = jl_friends[i].getText();
			Long friendID = getUserIDByString(str_ID);
			// 是好友
			if (targetID.equals(friendID)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 通过好友信息字符串截取好友ID
	 * 
	 * @param str
	 * @return
	 */
	public Long getUserIDByString(String str) {
		// 切取friendMessage中的好友昵称和好友ID
		String regex = "(?<=\\[).*(?=\\])";
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(str);
		Long userID = 0L;
		if (m.find()) {
			userID = Long.parseLong(m.group(0));
		}
		return userID;
	}

	/**
	 * 双击好友打开聊天窗口
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		int clickTwice = 2;
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == clickTwice) {
			// 获取好友ID
			String friendString = (((JLabel) e.getSource()).getText()).toString();
			// 切取friendMessage中的好友昵称和好友ID
			Long friendID = getUserIDByString(friendString);
			ChatFrame chatFrame = null;
			String IDs = userID + "" + friendID;
			// 判断之前是否打开过与这个好友的聊天窗口
			if ((chatFrame = ManageChatFrame.getChatFrame(IDs)) != null) {
				// 打开过：显示聊天窗口
				chatFrame.setState(JFrame.NORMAL);
			} else {
				// 没打开过：创建新的聊天窗口
				chatFrame = new ChatFrame(friendString, user);
				chatFrame.setVisible(true);
				// 加入到管理ChatFrame的HashMap中
				ManageChatFrame.addChatFrame(IDs, chatFrame);
			}
		}
	}

	// 当鼠标进入好友的JLabel时将背景设置为不透明，显示出黑灰色背景色
	@Override
	public void mouseEntered(MouseEvent e) {
		JLabel jLabel = (JLabel) e.getSource();
		// 设置jLabel字体颜色
		jLabel.setForeground(Color.RED);
		// 透明
		jLabel.setOpaque(true);
	}

	// 当鼠标移出时设置为透明，看不到黑灰色背景色
	@Override
	public void mouseExited(MouseEvent e) {
		JLabel jLabel = (JLabel) e.getSource();
		// 设置jLabel字体颜色
		jLabel.setForeground(Color.BLACK);
		// 不透明
		jLabel.setOpaque(false);
	}

	/**
	 * 创建错误对话框。
	 * 
	 * @param imageName 图片名
	 * @param message   弹窗信息
	 * @param type      对话框类型
	 */
	public void createDialog(String message) {
		// 弹窗图标的设置
		Icon icon = new ImageIcon("src/images/exclamation.png");
		// 因为对话框是阻塞的所以要加锁。我在非必要的时候用自定义窗口替代了
		synchronized (this) {
			JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.PLAIN_MESSAGE, icon);
		}
	}

	/**
	 * 检查账号ID的格式
	 * 
	 * @return 没问题则返回true
	 */
	public Boolean inspectUserID(String ID) {
		int length = ID.length();
		String pattern = "^\\d+$";
		if (length == 0) {
			// 输入为空
			createDialog("账号不能为空！");
			return false;
		} else if (length < 4 || length > 18) {
			// 账号名长度错误
			createDialog("账号在4~18个字符之间！");
			return false;
		} else if (!ID.matches(pattern)) {
			/*
			 * 账号是否是包含数字不匹配，说明有异常字符，返回真 弹出错误提示弹窗账号输入了异常字符
			 */
			createDialog("账号只允许使用数字！");
			return false;
		}
		return true;
	}

	/**
	 * @return User
	 */
	public User getUser() {
		return user;
	}

	// 当添加失败时为了继续显示添加好友对话框，需要让客户端线程获取按钮，执行点击事件
	/**
	 * @return 获取添加好友按钮
	 */
	public JButton getJbAddFriend() {
		return jbAddFriend;
	}

	// 同上
	/**
	 * @return 删除好友按钮
	 */
	public JButton getJbRemove() {
		return jbRemove;
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	public MainFrame() {}
}
