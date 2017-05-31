package com.glodon.gbq.web.model;

import java.io.Serializable;

public class ResultModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean result;
	private String errMsg;
	private Object data;
	private int errCode;

	public ResultModel() {
		this.result = true;
		this.errMsg = "";
		this.data = null;
		this.errCode = 0;
	}
	
	public ResultModel(boolean defaultResult) {
		this.result = defaultResult;
		this.errMsg = "";
		this.data = null;
		this.errCode = 0;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

}
