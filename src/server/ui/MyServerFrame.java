package server.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

import common.MsgType;
import common.Request;
import common.User;
import server.model.OnlineUserModel;
import server.model.RegistedUserModel;
import server.model.util.ServerUtils;

public class MyServerFrame extends JFrame implements ActionListener {
	/**  */
	private static final long serialVersionUID = 6877221476927658473L;
	/** 设置窗口的宽 */
	private final int F_WIDTH = 800;
	/** 设置窗口的高 */
	private final int F_HEIGTH = 700;

	/** 服务器端口号 */
	public final int PORT = 8888;

	/** 关闭关闭服务器按钮 */
	private JButton jbClose;
	/** 服务器发送系统公告按钮 */
	private JButton jbSend;
	/** 操作面板 */
	private JPanel jpOperate;
	/** 数据面板 */
	private JTabbedPane jTabbedPane;
	/** 时间面板 */
	private JLabel jlStatus;
	/** 服务器发送系统公告输入框 */
	private JTextField jtfMsg;

	/** 在线用户表格 */
	private OnlineUserModel onlineUserModel;
	/** 已注册的用户表格 */
	private RegistedUserModel registedUserModel;


	/**
	 * 创建服务器
	 */
	public MyServerFrame() {
		setTitle("服务器");

		// 设置窗口坐标,程序运行时窗口出现在屏幕中央
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - F_WIDTH) / 2, (dim.height - F_HEIGTH) / 2, F_WIDTH, F_HEIGTH);
		// 窗口大小不可以改变
		setResizable(false);
		// 设置本程序图标
		this.setIconImage(new ImageIcon("src/images/server.png").getImage());
		this.setLayout(new BorderLayout());

		// ——————————————————————————————————————————————————————————————————————————————————————————
		// ——————————————————————————————————————————操作面板——————————————————————————————————————————
		jpOperate = new JPanel();
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		jpOperate.setBorder(BorderFactory.createTitledBorder(border, "服务器监控", TitledBorder.LEFT, TitledBorder.TOP));

		// 关闭服务器按钮
		jbClose = new JButton("关闭服务器");
		jbClose.addActionListener(this);

		// 发送公告
		JLabel msgLabel = new JLabel("要发送的消息:");

		// 服务器要发送消息的输入框
		jtfMsg = new JTextField(30);

		// ——————————发送公告按钮——————————
		jbSend = new JButton("发送公告");
		jbSend.addActionListener(this);

		jpOperate.add(jbClose);
		jpOperate.add(msgLabel);
		jpOperate.add(jtfMsg);
		jpOperate.add(jbSend);

		this.add(jpOperate, BorderLayout.NORTH);

		// ——————————————————————————————————————————————————————————————————————————————————————————
		// ——————————————————————————————————————————数据面板——————————————————————————————————————————
		// 选修卡面板
		jTabbedPane = new JTabbedPane();
		// 在线用户列表
		onlineUserModel = new OnlineUserModel();

		JTable onlineUserTable = new JTable(onlineUserModel);
		setTable(onlineUserTable);
		onlineUserTable.setEnabled(false);
		jTabbedPane.addTab("在线用户列表", new JScrollPane(onlineUserTable));

		// 已注册用户列表
		registedUserModel = new RegistedUserModel();
		JTable registedUserTable = new JTable(registedUserModel);
		setTable(registedUserTable);
		registedUserTable.setEnabled(false);
		jTabbedPane.addTab("已注册用户列表", new JScrollPane(registedUserTable));

		this.add(jTabbedPane, BorderLayout.CENTER);
		// ——————————————————————————————————————————————————————————————————————————————————————————
		// ——————————————————————————————————————————状态面板——————————————————————————————————————————
		jlStatus = new JLabel("", SwingConstants.RIGHT);
		jlStatus.setFont(new Font("宋体", 0, 15));
		this.add(jlStatus, BorderLayout.SOUTH);
		jlStatus.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		// 用定时任务来显示当前时间,每1s刷新一次
		new Timer().scheduleAtFixedRate(new TimerTask() {
			DateFormat form = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

			@Override
			public void run() {
				jlStatus.setText("服务器端口号：" + PORT + "   " + "当前时间：" + form.format(new Date()) + "  ");
			}

		}, 0, 1000);

		// 显示整个窗口
		this.setVisible(true);
		// 设置服务器关闭提醒
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				logout();
			}
		});
	}

	private void setTable(JTable table) {
		table.setFillsViewportHeight(true);
		table.setRowSelectionAllowed(true);
		table.setFont(new Font("宋体", 0, 15));
		// 设置表格行宽
		table.setRowHeight(30);
		// 选择一整行
		table.setFillsViewportHeight(true);
		table.setRowSelectionAllowed(true);

		// 创建表格标题对象
		JTableHeader head = table.getTableHeader();
		// 设置表头大小
		head.setPreferredSize(new Dimension(head.getWidth(), 35));
		head.setFont(new Font("宋体", Font.PLAIN, 20));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbSend) {
			System.out.println("服务器发送按钮按下！");
		} else if (e.getSource() == jbClose) {
			logout();
		}
	}

	/**
	 * 关闭服务器
	 */
	private void logout() {
		// 设置弹窗的图标
		Icon icon = new ImageIcon("src/images/question.png");
		int select = JOptionPane.showConfirmDialog(this, "确定关闭服务器吗？\n关闭服务器将中断与所有客户端的连接!\n所有用户将强制下线！", "关闭",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icon);

		if (select == JOptionPane.YES_OPTION) {
			Request closeMsg = new Request();
			closeMsg.setFeedback(MsgType.CLOSE);
			ServerUtils.broadcast(closeMsg);
			System.exit(0);
		}
	}

	/**
	 * 添加用户信息至在线好友列表
	 * 
	 * @param user
	 */
	public void setOnlineUser(User user) {
		onlineUserModel.addRow(user);
	}
	
	/**
	 * 用户下线时移出在线好友列表
	 * 
	 * @param userID
	 */
	public void removeLogoutUser(Long userID) {
		onlineUserModel.removeRow(userID);
	}
	
	/**
	 * 添加用户信息至已注册好友列表
	 * 
	 * @param user
	 */
	public void setRegisteredUser(List<User> userList) {
		if (userList != null) {
			for(int i=0; i<userList.size(); i++) {
				User user = userList.get(i);
				registedUserModel.addRow(user);
			}
		}
	}
	
	/**
	 * 添加用户信息至已注册好友列表
	 * 
	 * @param user
	 */
	public void setRegisteredUser(User user) {
		registedUserModel.addRow(user);
	}
	

	/**
	 * @return 在线用户表格
	 */
	public OnlineUserModel getOnlineUserModel() {
		return onlineUserModel;
	}

	/**
	 * @return 已注册用户表格
	 */
	public RegistedUserModel getRegistedUserTable() {
		return registedUserModel;
	}
}
