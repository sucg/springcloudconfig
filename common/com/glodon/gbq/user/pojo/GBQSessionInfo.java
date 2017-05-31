package com.glodon.gbq.user.pojo;

import java.io.Serializable;

public class GBQSessionInfo  implements Serializable{
	
	private static final long serialVersionUID = -7768577196424291202L;

	private Integer userID;// 授权用户ID，对应user表
	
	private String gLDUserID;//用户在广联云库中的ID
	
	private String account;// 登录用户名
	
	private String accessToken;// 服务器token
	
	private String clientKey;// 客户端硬件标识
	
	private String clientType;//客户端类型
	
	private long lastAtiveTime;// 最后一次活动时刻
	
	private Integer companyId;// 公司ID
	
	private String authCompanyId;//公司authcompanyid
	
	private String gldToken;// 登录广联云的token
	
	private Integer userType;// 用户类型，10000试用账号、20000广联云账号，其他为授权账号
	
	public GBQSessionInfo(Integer userID, String gLDUserID, String account, String accessToken, String clientKey,
			long lastAtiveTime, Integer companyId, String gldToken, Integer userType) {
		super();
		this.userID = userID;
		this.gLDUserID = gLDUserID;
		this.account = account;
		this.accessToken = accessToken;
		this.clientKey = clientKey;
		this.lastAtiveTime = lastAtiveTime;
		this.companyId = companyId;
		this.gldToken = gldToken;
		this.userType = userType;
	}
	public GBQSessionInfo(Integer userID, String gLDUserID, String account, String accessToken,
			long lastAtiveTime, Integer companyId, String gldToken, Integer userType,String clientType) {
		super();
		this.userID = userID;
		this.gLDUserID = gLDUserID;
		this.account = account;
		this.accessToken = accessToken;
		this.lastAtiveTime = lastAtiveTime;
		this.companyId = companyId;
		this.gldToken = gldToken;
		this.userType = userType;
		this.clientType=clientType;
	}
	public GBQSessionInfo(Integer userID, String gLDUserID, String account, String accessToken, String clientKey,
			long lastAtiveTime, Integer companyId, String authCompanyId, String gldToken, Integer userType) {
		super();
		this.userID = userID;
		this.gLDUserID = gLDUserID;
		this.account = account;
		this.accessToken = accessToken;
		this.clientKey = clientKey;
		this.lastAtiveTime = lastAtiveTime;
		this.companyId = companyId;
		this.authCompanyId = authCompanyId;
		this.gldToken = gldToken;
		this.userType = userType;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	
	public Integer getUserID() {
		return userID;
	}
	
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	
	public String getLoginName() {
		return account;
	}
	
	public void setLoginName(String loginName) {
		this.account = loginName;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getClientKey() {
		return clientKey;
	}

	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public long getLastAtiveTime() {
		return lastAtiveTime;
	}

	public void setLastAtiveTime(long lastAtiveTime) {
		this.lastAtiveTime = lastAtiveTime;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getGldToken() {
		return gldToken;
	}

	public void setGldToken(String gldToken) {
		this.gldToken = gldToken;
	}
	
	public void setGLDUserID(String gLDUserID) {
		this.gLDUserID = gLDUserID;
	}
	
	public String getGLDUserID() {
		return gLDUserID;
	}

	public String getAuthCompanyId() {
		return authCompanyId;
	}

	public void setAuthCompanyId(String authCompanyId) {
		this.authCompanyId = authCompanyId;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	
}
