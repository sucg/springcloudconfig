package com.glodon.gbq.common.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import com.glodon.gbq.common.dao.Page;

public interface ICommonBaseDao<T>{
	
	/**
	 * @param sHql
	 * @param oParams
	 * @return 获取总数
	 */
	public List<T> querylist(String sHql, HashMap<String, Object> oParams);
	
	public List queryObjectList(String sHql, HashMap<String, Object> oParams);
	public List queryObjectListWithLimt(String sHql, HashMap<String, Object> oParams, int limitCount);
	/**
	 * 保存
	 * @param object
	 */
	public void save(T object); 
	
	/**
	 * 更新
	 * @param object
	 */
	public void update(T object);
	/**
	 * 删除
	 * @param object
	 */
	public void delete(T object);
	
	/**
	 * @param sHql
	 * @param oParams
	 * @return 批量操作
	 */
	public int executeUpdate(String sHql, HashMap<String, Object> oParams);
	
	/**
	 * @param id
	 * @return 根据id查找
	 */
	public T find(Integer id);
	
	/**
	 * @param sHql
	 * @param oParams
	 * @return 获取总数
	 */
	public int getCount(String sHql, HashMap<String, Object> oParams);
	
	public Object getSingleResult(String sHql, HashMap<String, Object> oParams);
	/**
	 * @param sHql
	 * @param oParams
	 * @return 分页查询
	 */
	public Page getPage(String countHql, String resultHql, int pageNo,
			int pageSize, HashMap<String, Object> oParams);
	
	public T get(Serializable id);

}
