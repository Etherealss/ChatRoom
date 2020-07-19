package common;

import java.io.Serializable;
import java.util.Date;

/**
 * 发送文件时的信息类。
 * 
 * @author 寒洲
 * 2020年6月15日
 * 寒洲
 */
public class FileInfo implements Serializable {
	
    /**  */
	private static final long serialVersionUID = 3212350894616990096L;
	/** 文件接收者 */
    private Long senderID;
	/** 文件发送者 */
    private Long recipientID;
    /** 源文件路径 */
    private String srcFilePath;
    /** 文件名 */
    private String fileName;
	/** 目标地IP地址 */
    private String recipientIP;
    /** 目标地端口 */
    private int recipientPort;
    /** 接收者存储文件的路径 */
    private String recipientFilePath;
    /**
   	 * @return 文件名
   	 */
   	public String getFileName() {
   		return fileName;
   	}
   	/**
   	 * @param 文件名
   	 */
   	public void setFileName(String fileName) {
   		this.fileName = fileName;
   	}
    /**
  	 * @return 发送者ID
  	 */
  	public Long getSenderID() {
  		return senderID;
  	}
  	/**
  	 * @param 发送者ID
  	 */
  	public void setSenderID(Long senderID) {
  		this.senderID = senderID;
  	}
  	/**
  	 * @return 发送者的文件路径
  	 */
    public String getSrcFilePath() {
        return srcFilePath;
    }
    /**
     * @param 发送者的文件路径
     */
    public void setSrcFilePath(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }
    /**
     * @return 接收者ID
     */
    public Long getRecipientID() {
    	return recipientID;
    }
    /**
     * @param 接收者ID
     */
    public void setRecipientID(Long recipientID) {
    	this.recipientID = recipientID;
    }
    /**
     * @return 接收者ID地址
     */
    public String getRecipientIP() {
        return recipientIP;
    }
    /**
     * @param 接收者ID地址
     */
    public void setRecipientIP(String recipientIP) {
        this.recipientIP = recipientIP;
    }
    /**
     * @return 接收者端口号
     */
    public int getRecipientPort() {
        return recipientPort;
    }
    /**
     * @param 接收者端口号
     */
    public void setRecipientPort(int recipientPort) {
        this.recipientPort = recipientPort;
    }
    /**
     * @return 接收者存储文件的路径
     */
    public String getRecipientFilePath() {
        return recipientFilePath;
    }
    /**
     * @param 接收者存储文件的路径
     */
    public void setRecipientFilePath(String destName) {
        this.recipientFilePath = destName;
    }
}
