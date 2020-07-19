package client.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class AvatarPanel extends JPanel {
	
	/**  */
	private static final long serialVersionUID = -740199784194126588L;
	ImageIcon imageIcon = null;
	/** 本Panel的宽 */
	int pWidth = 100;
	/** 本Panel的高 */
	int pHeight = pWidth;
	
	public AvatarPanel(String imagePath) {
		this.setSize(new Dimension(pWidth,pHeight));
		imageIcon = new ImageIcon(imagePath);
	}

	@Override
	protected void paintComponent(Graphics g) {
		// 本图片image大小
		int iWidth = imageIcon.getIconWidth();
		int iHeight = imageIcon.getIconHeight();
		int fitWidth = iWidth;
		int fitHeigth = iHeight;
		// 如果图片太宽，进行下面操作将图片等比例缩小
		if (pWidth < iWidth) {
			/*
			 * 先尝试以窗口之宽度作为图片宽度 按比例绘制图片 用窗口的宽作为图片的宽fitWidth 再用图片的原宽高比得出高度fitHeigth
			 */
			fitWidth = pWidth;
			fitHeigth = pWidth * iHeight / iWidth;
			// 若图片高度fitHeight超出宽度高度，就以窗口高度为图片高度，按比例绘制图片
			if (fitHeigth > pHeight) {
				fitHeigth = pHeight;
				fitWidth = pHeight * iWidth / iHeight;
			}
			// 设置图片大小，减去5px是为了美观
			imageIcon.setImage(imageIcon.getImage().getScaledInstance(
					fitWidth , fitHeigth , Image.SCALE_DEFAULT));
		}else if(pWidth > iWidth) {
			fitWidth = pWidth;
			fitHeigth = iHeight* pWidth/iWidth;
		}
		g.drawImage(imageIcon.getImage(), 0, 0, fitWidth, fitHeigth, (JPanel)this);
	}
}
