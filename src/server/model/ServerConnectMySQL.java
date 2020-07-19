package server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Field;

import common.Request;
import common.User;
import server.model.util.JDBCUtils;
import common.MsgType;

public class ServerConnectMySQL {

	/** 与数据库的连接 */
	Connection conn = null;


	/**
	 * 传入与数据库的连接
	 * 
	 * @param conn
	 */
	public ServerConnectMySQL(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 注册新用户
	 * 
	 * @param user
	 * @return 用于服务器反馈的Request<br>
	 *         1、未知错误：REGISTER_FAIL<br>
	 *         2、ID已存在：REGISTER_ID_EXIST<br>
	 *         3、注册成功：REGISTER_YES<br>
	 */
	public Request registerNewUser(User user) {

		Request feedback = new Request();
		// 默认设置为注册失败
		feedback.setFeedback(MsgType.REGISTER_FAIL);

		Long userID = user.getID();
		String password = user.getPassword();
		String sex = user.getSex();
		String avatar = user.getAvatarPath();
		String nickname = user.getNickname();

		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;

		// 查询账号是否存在
		String sql_select = "SELECT `user_id` FROM `user` WHERE `user_id`=" + userID;
		// 创建用户
		String sql_insert = "INSERT INTO USER VALUES(" + userID + "," + password + ",'" + nickname + "','" + sex + "','"
				+ avatar + "')";

		// 执行sql语句
		try {
			ps1 = conn.prepareStatement(sql_select);
			// 获取结果集
			rs = ps1.executeQuery();

			if (rs.next()) {
				// 账号存在
				feedback.setFeedback(MsgType.REGISTER_ID_EXIST);
			} else {
				// 创建用户
				ps2 = conn.prepareStatement(sql_insert);
				ps2.execute();
				feedback.setFeedback(MsgType.REGISTER_YES);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResources(ps1, rs);
			try {
				if(ps2!=null) {
					ps2.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return feedback;
	}


	/**
	 * 登录判断<br>
	 * 传入user，通过其中的账号密码判断用户是否存在、密码是否正确
	 * 
	 * @param user
	 * @return 用于服务器反馈的Request<br>
	 *         1、用户不存在：USER_NOT_FOUND<br>
	 *         2、用户密码错误：LOGIN_WRONG<br>
	 *         3、登录成功：LOGIN_YES<br>
	 */
	public Request verifyLogin(User user) {
		Request feedback = new Request();
		// 默认用户不存在
		feedback.setFeedback(MsgType.USER_NOT_FOUND);

		Long userID = user.getID();
		String password = user.getPassword();

		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;

		String sql1 = "SELECT `user_id` FROM `user` WHERE `user_id`=" + userID;
		String sql2 = "SELECT `user_id` ID,`password`,`user_nickname` nickname,`sex`,`avatars` avatarPath"
				+ " FROM `user` WHERE `user_id`=" + userID + " AND `password`=" + password + "";
		try {
			// 执行MySQL语句并查询
			ps1 = conn.prepareStatement(sql1);
			// 获取结果集
			rs1 = ps1.executeQuery();
			if (rs1.next()) {
				// 用户存在!
				// 执行MySQL语句并查询
				ps2 = conn.prepareStatement(sql2);
				// 获取结果集 这里不知道为啥说没有关闭
				rs2 = ps2.executeQuery();
				ResultSetMetaData rsmd = rs2.getMetaData();
				// 通过ResultSetMetaData获取结果集中的列数
				int columnCount = rsmd.getColumnCount();
				if (rs2.next()) {
					// 处理结果集一行数据中的每一个列
					for (int i = 0; i < columnCount; i++) {
						// 获取列值
						Object columValue = rs2.getObject(i + 1);
						// 获取每个列的列名
						String columnLabel = rsmd.getColumnLabel(i + 1);

						// 给user对象指定的columnName属性，赋值为columValue：通过反射
						Field field = User.class.getDeclaredField(columnLabel);
						field.setAccessible(true);
						field.set(user, columValue);
					}
					// 将用户设置为在线
					user.setOnlineState(true);
					// 将完整的用户对象添加到反馈包中
					feedback.setUser(user);
					// 密码正确
					System.out.println(user.toString());
					feedback.setFeedback(MsgType.LOGIN_YES);

				} else {
					// 密码错误
					feedback.setFeedback(MsgType.LOGIN_WRONG);
				}
			}
			// 因为默认用户不存在，所以如果不进入if语句的话也可以直接返回。
			// 返回语句在catch之后

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResources(ps1, rs1);
			JDBCUtils.closeResources(ps2, rs2);
		}

		return feedback;
	}

	/**
	 * 添加或删除好友关系
	 * 
	 * @param ID1 请求方
	 * @param ID2 被添加方
	 */
	public MsgType changeRelationship(Long ID1, Long ID2,MsgType msgType) {
		System.out.println("scm:添加好友：" + ID1 + " 加 ：" + ID2);
		PreparedStatement ps = null;
		ResultSet rs = null;
		// 查询是否存在要添加的好友用户
		String sqlExist = "SELECT `user_id` FROM `user` WHERE `user_id`=" + ID2;
		// 执行MySQL语句并查询
		try {
			ps = conn.prepareStatement(sqlExist);
			// 获取结果集
			rs = ps.executeQuery();
			if (rs.next()) {
				// 用户存在!
				if(msgType == MsgType.ADD_FRIEND) {
					String sqlAdd = "INSERT INTO `relationship` VALUES (?,?);";
					//数据库通用增删改方法
					JDBCUtils.universalCUD(sqlAdd, ID1, ID2);
					JDBCUtils.universalCUD(sqlAdd, ID2, ID1);
					System.out.println("ServerConnectMySQL:添加了好友关系！");
					// 用户已添加
					return MsgType.FRIEND_ADDED;
				}else {
					String sqlRemove = "DELETE FROM `relationship` WHERE (`user_id`=? AND `friend_id`=?) OR (`friend_id`=? AND `user_id`=?);";
					//数据库通用增删改方法
					JDBCUtils.universalCUD(sqlRemove, ID1, ID2,ID1,ID2);
					System.out.println("ServerConnectMySQL:删除了好友！");
					// 好友已删除
					return MsgType.REMOVE;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 没有这个用户
		return MsgType.USER_NOT_FOUND;

	}

	
	
	
	/**
	 * 获取用户的好友列表
	 * 
	 * @param userID
	 * @return 存储User的ArrayList
	 */
	public List<User> getFriends(Long userID) {
		List<User> friends = new ArrayList<User>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		// 通过userID获取其好友的ID
		String sql = "SELECT `friend_id` FROM `relationship` WHERE `user_id`=" + userID;
		try {
			ps = conn.prepareStatement(sql);
			// 获取结果集
			rs = ps.executeQuery();
			int i = 1;
			if (rs.next()) {
				do {
					// 获取结果集的friendID
					Long columValue = rs.getLong(1);
					User user = getUser(columValue);
					if (user != null) {
						// 添加到List中
						friends.add(user);
					} else {
						System.out.println("ServerConnectMySQL:获取好友出现意外！");
					}
				} while (rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResources(ps, rs);
		}

		return friends;
	}

	/**
	 * 通过userID获取User对象
	 * 
	 * @param userID
	 * @return User
	 */
	public User getUser(Long userID) {
		User user = null;
		// 执行MySQL语句并查询(不查询密码)
		String sql = "SELECT `user_id` ID,`user_nickname` nickname,`sex`,`avatars` avatarPath"
				+ " FROM `user` WHERE `user_id`=" + userID;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		try {
			ps2 = conn.prepareStatement(sql);
			// 获取结果集
			rs2 = ps2.executeQuery();
			ResultSetMetaData rsmd = rs2.getMetaData();
			// 通过ResultSetMetaData获取结果集中的列数
			int columnCount = rsmd.getColumnCount();
			if (rs2.next()) {
				user = new User();
				// 处理结果集一行数据中的每一个列
				for (int i = 0; i < columnCount; i++) {
					// 获取列值
					Object columValue = rs2.getObject(i + 1);
					// 获取每个列的列名
					String columnLabel = rsmd.getColumnLabel(i + 1);

					// 给user对象指定的columnName属性，赋值为columValue：通过反射
					Field field = User.class.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(user, columValue);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResources(ps2, rs2);
		}

		return user;
	}

	/**
	 * 获取已注册用户
	 * 
	 * @return 储存User的ArrayList
	 */
	public List<User> getRegistedUser() {
		List<User> registedUser = new ArrayList<User>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		// 通过userID获取其好友的ID
		String sql = "SELECT `user_id` ID, `password`, `user_nickname` nickname, `sex` FROM `user`";
		try {
			ps = conn.prepareStatement(sql);
			// 获取结果集
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			// 通过ResultSetMetaData获取结果集中的列数
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				// 处理结果集一行数据中的每一个列
				User user = new User();
				for (int i = 0; i < columnCount; i++) {
					// 获取列值
					Object columValue = rs.getObject(i + 1);
					// 获取每个列的列名
					String columnLabel = rsmd.getColumnLabel(i + 1);
					
					// 给user对象指定的columnName属性，赋值为columValue：通过反射
					Field field = User.class.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(user, columValue);
				}
				registedUser.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResources(ps, rs);
		}
		
		return registedUser;
	}

	public ServerConnectMySQL() {}

}
