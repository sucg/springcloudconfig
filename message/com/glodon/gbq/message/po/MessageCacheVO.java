package com.glodon.gbq.message.po;

import java.io.Serializable;

/***
 * 作为小缓存的封装类 小缓存指的是集群中每个节点中记录的信息， 包括了
 * sessionid（长连接的session） 
 * clientPath（用户订阅的路径）
 * token （用户登录时候的token）
 * account （用户的账户名称）
 * 
 * @author sucg
 *
 */
public class MessageCacheVO implements Serializable{

	private String account;

	private String clientPath;

	private String token;

	private String sessionID;

	public MessageCacheVO() {
	}

	public MessageCacheVO(String account, String clientPath, String token, String sessionID) {
		super();
		this.account = account;
		this.clientPath = clientPath;
		this.token = token;
		this.sessionID = sessionID;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getClientPath() {
		return clientPath;
	}

	public void setClientPath(String clientPath) {
		this.clientPath = clientPath;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

}
