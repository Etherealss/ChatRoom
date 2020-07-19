package client.model.util;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * 储存文件的工具类
 * 
 * @author 寒洲
 * 2020年6月19日
 * 寒洲
 */
public class SavaFileUtils {
	
	/**
	 * 判断是否存同名文件并返回包含储存路径的File对象
	 * 
	 * @param savePath
	 * @return File
	 */
	public static File getFileToSave(String savePath) {
		// 获取文件名
		// 判断传输的位置是否有同名文件，如果有，则在传输的文件名后加上（n）
		// n表示第几个同名文件夹
		int n = 2;
		// 判断是否存在同名文件
		File file = new File(savePath);
		while (file.exists()) {
			/*
			 * 获取文件后缀名前的英文点位置
			 * 将字符串分割成前后两段，后段是文件后缀名 在两段字符串中加上（n）
			 */
			int index = savePath.lastIndexOf(".");
			String front = savePath.substring(0, index);
			String end = savePath.substring(index, savePath.length());
			String savePath2 = front + "(" + n++ + ")" + end;
			// 最终的文件名
			file = new File(savePath2);
		}
		return file;
	}
	
	/**
	 * 检查用户默认存储位置是否存在 若不可用会创建文件夹
	 * 
	 * @param defaultPath
	 */
	private static void inspectImageFolder(String defaultPath) {
		// 储存照片的文件夹
		File file = new File(defaultPath + "/images");
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	/**
	 * 检查用户默认存储位置是否存在 若不可用会创建文件夹
	 */
	public static void inspectFolder(String defaultPath) {
		// 储存文件的文件夹
		File file1 = new File(defaultPath + "/files");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		// 储存照片的文件夹
		File file2 = new File(defaultPath + "/images");
		if (!file2.exists()) {
			file2.mkdirs();
		}
	}


	/**
	 * 发送者将图片也保存到自己的默认储存位置下
	 * 
	 * @param defaultPath
	 * @param imagePath
	 * @param imageName
	 */
	public static void savaImage(String defaultPath,String imagePath, String imageName) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(imagePath));

			// 检查储存位置是否存在，若没有则创建文件夹
			inspectImageFolder(defaultPath);

			// 储存路径
			String savePath = defaultPath + "/images/" + imageName;
			File imageFile = getFileToSave(savePath);
			bos = new BufferedOutputStream(new FileOutputStream(imageFile));

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 将文件的传输记录作为按钮，以打开文件
	 * 
	 * @param fileName
	 * @param filePath
	 * @return JButton
	 */
	public static JButton getFileButton(String fileName, String filePath) {
		// 按钮的外观
		ImageIcon fileIcon = new ImageIcon("src/images/file4.png");
		JButton fileButton = new JButton(fileName, fileIcon);
		// 透明
		fileButton.setContentAreaFilled(false);
		// 按钮的内容布局
		fileButton.setHorizontalTextPosition(SwingConstants.CENTER);
		fileButton.setVerticalTextPosition(SwingConstants.BOTTOM);

		// 文件按钮添加打开文件的鼠标事件
		fileButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 判断环境是否支持桌面类
				if (Desktop.isDesktopSupported()) {
					// 创建对象
					Desktop desk = Desktop.getDesktop();
					try {
						// 打开文件
						desk.open(new File(filePath));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			// 不需要的方法
			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}
		});

		return fileButton;
	}
}
