package com.glodon.gbq.common.dao;

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//存放结果集 
	private List result;
	private static int DEF_PAGE_VIEW_SIZE = 20;
	private int page;
	private int pageSize;
	private int count;
	public Page(List lstResult,int page, int pageSize){
	    result = lstResult;
	    this.page = (page <= 0 ? 1 : page);
	    this.pageSize = (pageSize <= 0 ? DEF_PAGE_VIEW_SIZE : pageSize);
	}
	public Page(int count, int page, int pageSize) {
		this.count = (count <0 ? 0 : count);;
		this.page = (page <= 0 ? 1 : page);
	    this.pageSize = (pageSize <= 0 ? DEF_PAGE_VIEW_SIZE : pageSize);
	}
	public List getResult() {
		return result;
	}
	public void setResult(List result) {
		this.result = result;
	}
	public int getPage(){
		return page <= 0 ? 1 : page;
	}
	public void setPage(int page){
		this.page = page;
	}
	public int getPageSize(){
		return pageSize <= 0 ? DEF_PAGE_VIEW_SIZE : pageSize;
	}
	public void setPageSize(int pageSize){
	    this.pageSize = pageSize;
	}
	public int getCount(){
	    return count;
	}
	public void setCount(int count){
	    this.count = (count < 0 ? 0 : count);
	    if (this.count == 0)
	      page = 0;
	}
	public int getPages(){
	    return (count + getPageSize() - 1) / getPageSize();
	}
	public int getStartNo(){
	    return (getPage() - 1) * getPageSize() + 1;
	}
	public int getEndNo(){
	    return Math.min(getPage() * getPageSize(), count);
	}
	public int getPrePageNo(){
	    return Math.max(getPage() - 1, 1);
	}
	public int getNextPageNo(){
	    return Math.min(getPage() + 1, getPages());
	}
}
