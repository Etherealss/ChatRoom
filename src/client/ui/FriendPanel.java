package client.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import common.User;

/**
 * 好友/群组列表界面
 * 
 * @author 寒洲 2020年5月31日 寒洲
 */
public class FriendPanel extends JPanel implements ActionListener{
	/** FriendPanel */
	private static final long serialVersionUID = -5697882189515495533L;
	/** 设置好友列表的宽 */
	public static final int WIDTH = 300;
	/** 群组数量 */
	public int groupNum = 1;

	//好友卡片的控件
	private JScrollPane jspFrined;
	private JPanel jpFriend;
	private JPanel friendsPanel;
	private JButton jb1_Friend, jb1_Groups;

	//群组卡片的控件
	private JScrollPane jspGroup;
	private JPanel groupsPanel;
	private JPanel jpGroup;
	private JButton jb2_Friend, jb2_Groups;

	//群名，只有一个群（而且我还没时间做啊啊啊）
	private String groupName = "群聊";

	//好友和群组列表的JLabel
	private JLabel[] jp_groups = null;
	private JLabel[] jl_friends = null;
	
	/**
	 * @return the jl_friends
	 */
	public JLabel[] getJl_friends() {
		return jl_friends;
	}

	private CardLayout cardLayout;
	//创建空面板
	public FriendPanel() {}
	
	/**
	 * 创建好友列表
	 * 
	 * @param userID
	 */
	public FriendPanel(List<User> friends) {
		this.setSize(WIDTH, 800);
		this.setBackground(Color.BLACK);
		// ——————————————————————————————————卡片1 好友列表——————————————————————————————————
		// 一个界面最多容纳15个好友
		int preferNum = 15;
		//好友数量
		int friendNum = friends.size();
		jpFriend = new JPanel(new BorderLayout());
		// 确保好友列表的大小
		if(friendNum > preferNum) {
			friendsPanel = new JPanel(new GridLayout(friendNum, 1, 5, 5));
		}else {
			friendsPanel = new JPanel(new GridLayout(15, 1, 5, 5));
		}
		// 添加好友
		jl_friends = new JLabel[friendNum];
		for (int i = 0; i < friendNum; i++) {
			User user = friends.get(i);
			// 设置用户头像、昵称
			Long frinedID = user.getID(); 
			String friendNickname = user.getNickname();
			String friendMsg = friendNickname + " [" + frinedID + "]";
			
			String friendAvatar = user.getAvatarPath();
			Icon friendIcon = new ImageIcon(friendAvatar);
			jl_friends[i] = creatJLabel(friendMsg, friendIcon);
//			jpFriend.add(jl_friends[i]);
			/*
			 * 获取用户的在线状态
			 * 若在线则为true，则设置为可用
			 * 若为false则不在线，设置为不可用
			 */
			if (user.getOnlineState()) {
				jl_friends[i].setEnabled(true);
				//在线好友显示在前面
				friendsPanel.add(jl_friends[i]);
			}else {
				jl_friends[i].setEnabled(false);
				//不在线好友暂不添加
			}
		}
		//添加不在线的好友
		for (int i = 0; i < friendNum; i++) {
			if(!jl_friends[i].isEnabled()) {
				friendsPanel.add(jl_friends[i]);
			}
		}
		// 处理按钮
		jb1_Friend = creatJButton("我的好友");
		jb1_Groups = creatJButton("群聊");
		// 添加滚动条
		jspFrined = new JScrollPane(friendsPanel);

		jpFriend.add(jb1_Friend, BorderLayout.NORTH);
		jpFriend.add(jspFrined, BorderLayout.CENTER);
		jpFriend.add(jb1_Groups, BorderLayout.SOUTH);

		// ——————————————————————————————————卡片2 群组列表——————————————————————————————————

		jpGroup = new JPanel(new BorderLayout());
		if(groupNum > preferNum) {
			groupsPanel = new JPanel(new GridLayout(groupNum, 1, 5, 5));
		}else {
			groupsPanel = new JPanel(new GridLayout(15, 1, 5, 5));
		}
		// 添加群组
		jp_groups = new JLabel[groupNum];
		//群组图标
		Icon groupIcon = new ImageIcon("src/images/groups.png");
		for (int i = 0; i < groupNum; i++) {
			// 设置群组图标、群名
			jp_groups[i] = creatJLabel(groupName + (i+1), groupIcon);
			groupsPanel.add(jp_groups[i]);
		}
		// 处理按钮
		jb2_Friend = creatJButton("我的好友");
		jb2_Groups = creatJButton("群聊");
		// 添加滚动条
		jspGroup = new JScrollPane(groupsPanel);

		JPanel jp_button = new JPanel(new GridLayout(2,1));
		jp_button.add(jb2_Friend);
		jp_button.add(jb2_Groups);
		jpGroup.add(jp_button, BorderLayout.NORTH);
		jpGroup.add(jspGroup, BorderLayout.CENTER );

		
		//添加到这个FriendPanel中
		cardLayout = new CardLayout();
		this.setLayout(cardLayout);
		this.add(jpFriend, "1");
		this.add(jpGroup, "2");

	}

	/** 获取好友列表的宽 */
	@Override
	public int getWidth() {
		return WIDTH;
	}

	/**
	 * 创建固定样式的Label
	 * 
	 * @param title
	 * @param icon
	 * @param horizontalAlignment
	 * @return a JLabel
	 */
	private JLabel creatJLabel(String title, Icon icon) {
		//设置JLabel的内容
		JLabel label = new JLabel(title, icon, JLabel.LEFT);
		label.setPreferredSize(new Dimension(0,40));
		label.setFont(new Font("微软雅黑", 0, 18));
		//设置JLabel的默认背景色，同时设置背景透明
		//添加鼠标事件后，当鼠标悬停时将背景改为不透明，颜色就会显现出来
		label.setBackground(new Color(211,211,211));
		label.setOpaque(false);
		return label;
	}

	/**
	 * 创建固定样式的按钮
	 * 
	 * @return a JButton
	 */
	private JButton creatJButton(String title) {
		JButton jb = new JButton(title);
		jb.setFont(new Font("微软雅黑", 0, 18));
		jb.setPreferredSize(new Dimension(WIDTH,50));
		jb.addActionListener(this);
		return jb;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jb1_Groups) {
			cardLayout.show(this, "2");
		} else if (e.getSource() == jb2_Friend) {
			cardLayout.show(this, "1");
		}
	}

}
