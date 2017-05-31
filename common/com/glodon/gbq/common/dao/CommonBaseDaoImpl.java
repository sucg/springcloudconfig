package com.glodon.gbq.common.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.glodon.gbq.common.dao.Page;

public class CommonBaseDaoImpl<T> implements ICommonBaseDao<T> {
	
	private SessionFactory sessionFactory;
	private Class<T> entityClass;
	private String pkname;
	public CommonBaseDaoImpl() {
		this.entityClass=(Class<T>) ((ParameterizedType) this.getClass()  
                .getGenericSuperclass()).getActualTypeArguments()[0];  
		getPkname();
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<T> querylist(String sHql, HashMap<String, Object> oParams) {
		Query oQuery = getSession().createQuery(sHql);
		if (null != oParams && oParams.isEmpty() == false) {
			Set<String> keys = oParams.keySet();
			for (String string : keys) {
				oQuery.setParameter(string, oParams.get(string));
			}
		}
		return oQuery.list();
	}
	
	@Override
	public List queryObjectList(String sHql, HashMap<String, Object> oParams){
		Query oQuery = getSession().createQuery(sHql);
	    
		if (null != oParams && oParams.isEmpty() == false) {
			Set<String> keys = oParams.keySet();
			for (String string : keys) {
				oQuery.setParameter(string, oParams.get(string));
			}
		}
		return oQuery.list();
	}
	
	@Override
	public List queryObjectListWithLimt(String sHql, HashMap<String, Object> oParams, int limitCount){
		Query oQuery = getSession().createQuery(sHql);
		oQuery.setFirstResult(0);
		oQuery.setMaxResults(limitCount);
	    
		if (null != oParams && oParams.isEmpty() == false) {
			Set<String> keys = oParams.keySet();
			for (String string : keys) {
				oQuery.setParameter(string, oParams.get(string));
			}
		}
		return oQuery.list();
	}
	
	@Override
	public void save(T object) {
		getSession().save(object);
	}

	@Override
	public void update(T object) {
		getSession().update(object);
	}

	@Override
	public void delete(T object) {
		getSession().delete(object);
	}

	@Override
	public int executeUpdate(String sHql, HashMap<String, Object> oParams) {
		Query oQuery = getSession().createQuery(sHql);
		if (null != oParams && oParams.isEmpty() == false) {
			Set<String> keys = oParams.keySet();
			for (String string : keys) {
				oQuery.setParameter(string, oParams.get(string));
			}
		}
		return oQuery.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T find(Integer nId) {
		return (T) getSession().get(entityClass, nId);
	}
	@Override
	public Page getPage(String sCountHql, String sResultHql, int nPageNo,
			int nPageSize, HashMap<String, Object> oParams) {
		int nCount =  getCount(sCountHql, oParams);
		Page oPage = new Page(nCount, nPageNo, nPageSize);
		if(nCount > 0){
			Query oQuery = getSession().createQuery(sResultHql);
			if (null != oParams && oParams.isEmpty() == false) {
				Set<String> oKeys = oParams.keySet();
				for (String sKey : oKeys) {
					oQuery.setParameter(sKey, oParams.get(sKey));
				}
			}
			if(nCount > nPageSize){
				oQuery.setFirstResult(oPage.getStartNo()).setMaxResults(oPage.getEndNo());
			}
			List resultList = oQuery.list();
			oPage.setResult(resultList);
		}
		return oPage;
	}
	@Override
	public int getCount(String sHql, HashMap<String, Object> oParams) {
		Query oQuery = getSession().createQuery(sHql);
		if (null != oParams && oParams.isEmpty() == false) {
			Set<String> keys = oParams.keySet();
			for (String sKey : keys) {
				oQuery.setParameter(sKey, oParams.get(sKey));
			}
		}
		List resultList = oQuery.list();
		for (Object object : resultList) {
			Long oCount = (Long) object;
			if(oCount > 0){
				return Integer.parseInt(oCount+"");
			}
		}
		return 0;
	}
	@Override
	public Object getSingleResult(String sHql, HashMap<String, Object> oParams) {
		Query oQuery = getSession().createQuery(sHql);
		if (null != oParams && oParams.isEmpty() == false) {
			Set<String> keys = oParams.keySet();
			for (String sKey : keys) {
				oQuery.setParameter(sKey, oParams.get(sKey));
			}
		}
		oQuery.setMaxResults(1);
		List resultList = oQuery.list();
		for (Object object : resultList) {
			return object;
		}
		return null;
	}
	
	/**
	 * 获取主键名称
	 * @return
	 */
	public String getPkname() {
		Field[] fields = this.entityClass.getDeclaredFields();//反射类字段
		for (Field field : fields) {
			field.isAnnotationPresent(Id.class);
			this.pkname=field.getName();
			break;
		}
		return pkname;
	}
	@SuppressWarnings("unchecked")
	@Override
	public T get(Serializable id) {
		return (T) getSession().get(entityClass, id);
	}

}
