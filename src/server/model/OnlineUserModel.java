package server.model;

import javax.swing.table.DefaultTableModel;

import common.User;

public class OnlineUserModel extends DefaultTableModel {


	public OnlineUserModel() {
		this.addColumn("账号");
		this.addColumn("昵称");
		this.addColumn("性别");
	}
	
	/**
	 * 添加一行数据
	 * 
	 * @param user
	 */
	public void addRow(User user) {
		String[] data = new String[3];
		data[0] = user.getID().toString();
		data[1] = user.getNickname();
		if(("f").equals(user.getSex())) {
			data[2] = "小哥哥";
		}else {
			data[2] = "小姐姐";
		}
		this.addRow(data);
	}
	
	/**
	 * 移除一行数据
	 * 
	 * @param user
	 */
	public void removeRow(Long userID) {
		String ID = userID.toString();
		int row = 0;
		for(int i=0; i<this.getRowCount(); i++) {
			Object o = this.getValueAt(i, 0);
			if(ID.equals(o.toString())) {
				row = i;
				break;
			}
		}
		this.removeRow(row);
	}
}
