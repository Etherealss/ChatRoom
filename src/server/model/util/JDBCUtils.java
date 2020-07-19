package server.model.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 操作数据库的工具类
 * 
 * @author 寒洲 2020年6月6日
 * 
 */
public class JDBCUtils {
	/** 数据库端口号 */
	public static int PORT;

	/**
	 * @return 返回数据库端口号
	 */
	public static int getPort() {
		return PORT;
	}

	/**
	 * 获取数据库的连接
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		// 读取服务器配置文件
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("serverconfig.properties");

		Properties pros = new Properties();
		pros.load(is);

		String user = pros.getProperty("user");
		String password = pros.getProperty("password");
		String url = pros.getProperty("url");
		String driverClass = pros.getProperty("driverClass");

		PORT = Integer.parseInt(pros.getProperty("port"));

		// 获取连接
		Class.forName(driverClass);
		Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
	}

	/**
	 * 通用的数据库增、删、改方法
	 * 
	 * @param sql
	 * @param args
	 */
	public static void universalCUD(String sql, Object... args) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			// 获取数据库的连接
			conn = JDBCUtils.getConnection();
			// 预编译sql语句
			ps = conn.prepareStatement(sql);
			// 填充占位符
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}
			// 执行MySQL语句
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 资源的关闭
			closeJDBC(conn, ps);
		}
	}

	/**
	 * 关闭与数据库的连接
	 * 
	 * @param conn
	 * @param ps
	 */
	public static void closeJDBC(Connection conn, Statement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭资源
	 * 
	 * @param ps
	 */
	public static void closeResources(Statement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭资源
	 * 
	 * @param ps
	 * @param rs
	 */
	public static void closeResources(Statement ps, ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭与数据库的连接
	 * 
	 * @param conn
	 * @param ps
	 * @param rs
	 */
	public static void closeJDBC(Connection conn, Statement ps, ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
