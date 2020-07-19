package server.model;

import javax.swing.table.DefaultTableModel;

import common.User;

public class RegistedUserModel extends DefaultTableModel{

	public RegistedUserModel() {
		this.addColumn("账号");
		this.addColumn("密码");
		this.addColumn("昵称");
		this.addColumn("性别");
	}
	
	/**
	 * 添加一行数据
	 * 
	 * @param user
	 */
	public void addRow(User user) {
		String[] data = new String[4];
		data[0] = user.getID().toString();
		data[1] = user.getPassword();
		data[2] = user.getNickname();
		if(("f").equals(user.getSex())) {
			data[3] = "小哥哥";
		}else {
			data[3] = "小姐姐";
		}
		this.addRow(data);
	}
}
