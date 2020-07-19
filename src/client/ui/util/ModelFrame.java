package client.ui.util;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * 模板框架类
 * 
 * @author 寒洲
 * @date:May 20, 2020
 */
@SuppressWarnings("serial")
public class ModelFrame extends JFrame {

	/** 窗口的宽 */
	private int mWidth;
	/** 窗口的高 */
	private int mHeight;
	/** 账号ID输入框 */
	private JTextField jtfUserID;
	/** 密码输入框 */
	private JPasswordField jpf_password;

	/**
	 * 创建模板窗口，居中显示
	 * 
	 * @param title
	 * @param width
	 * @param height
	 */
	public ModelFrame(String title, int width, int height) {
		mWidth = width;
		mHeight = height;
		jtfUserID = new JTextField();
		jpf_password = new JPasswordField();
		// 设置窗口名
		setTitle(title);
		// 设置窗口坐标,程序运行时窗口出现在屏幕中央
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - mWidth) / 2, (dim.height - mHeight) / 2, mWidth, mHeight);
		// 窗口大小不可以改变
		setResizable(false);
		// 窗口可关闭
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 显示窗口
		this.setVisible(true);
	}

	/**
	 * 创建对话框
	 * 
	 * @param imageName 图片名
	 * @param message   弹窗信息
	 * @param type      对话框类型
	 */
	public void createDialog(String imageName, String message, String type) {
		// 弹窗图标的设置
		Icon icon = new ImageIcon("src/images/" + imageName);
		JOptionPane.showMessageDialog(this, message, type, JOptionPane.PLAIN_MESSAGE, icon);
	}

	// 因为错误对话框经常要用到所以额外写一个方法
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
		JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.WARNING_MESSAGE, icon);
	}

	/**
	 * 检查账号
	 * 
	 * @return 没问题则返回true
	 */
	public Boolean inspectUserID() {
		String text = jtfUserID.getText();
		int length = text.length();
		if (length == 0) {
			// 输入为空
			createDialog("账号不能为空！");
			return false;
		} else if (length < 4 || length > 18) {
			// 账号名长度错误
			createDialog("账号在4~18个字符之间！");
			return false;
		} else if (regexID(jtfUserID.getText())) {
			// 账号输入了异常字符
			createDialog("账号只允许使用数字！");
			return false;
		}
		return true;
	}

	/**
	 * 检查密码 密码只允许包含大小写字母、数字、下划线和英文句点
	 * 
	 * @return 没问题则返回true
	 */
	public Boolean inspectPassword() {
		String password = String.valueOf(jpf_password.getPassword());
		int length = password.length();
		if (length == 0) {
			// 输入为空
			createDialog("密码不能为空！");
			return false;
		} else if (length < 6 || length > 20) {
			// 密码长度错误
			createDialog("密码在6~20个字符之间！");
			return false;
		} else if (regexPassword(password)) {

			// 密码输入了异常字符
			createDialog("密码只允许包含大小写字母、数字、下划线和英文句点！");
			return false;
		}
		return true;
	}

	/**
	 * 判断账号ID是否满足正则表达式 <br>
	 * 如果没有问题，返回false <br>
	 * 如果有异常字符，返回true<br>
	 * 
	 * @return Boolean
	 */
	public Boolean regexID(String userID) {
		// 账号是否是包含数字
		String pattern = "^\\d+$";
		if (userID.matches(pattern)) {
			// 账号名匹配
			return false;
		}
		// 不匹配，说明有异常字符，返回真，弹出错误提示弹窗
		return true;
	}

	/**
	 * 判断密码是否满足正则表达式 <br>
	 * 密码是否只包含大小写字母、数字和英文点<br>
	 * 如果没有问题，返回false <br>
	 * 如果有异常字符，返回true<br>
	 * 
	 * @return Boolean
	 */
	public Boolean regexPassword(String password) {
		// 密码是否只包含大小写字母、数字和英文点
		String pattern = "^[a-zA-Z0-9_.]+$";
		if (password.matches(pattern)) {
			// 密码匹配，返回false
			return false;
		}
		return true;
	}

	/**
	 * @return 账号ID输入框
	 */
	public JTextField getJtfUserID() {
		return jtfUserID;
	}

	/**
	 * @return 密码输入框
	 */
	public JPasswordField getJpfPassword() {
		return jpf_password;
	}
}
