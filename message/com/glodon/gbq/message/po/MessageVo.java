package com.glodon.gbq.message.po;

//import java.io.Externalizable;
import java.io.Serializable;

public class MessageVo implements Serializable{
	
	private String account;
	
	private String message;
	
	private String clientType;
	
	private String messageType;
	
	public MessageVo() {
	}
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public MessageVo(String account, String message, String clientType,
			String messageType) {
		super();
		this.account = account;
		this.message = message;
		this.clientType = clientType;
		this.messageType = messageType;
	}

}
