package com.glodon.common.cache;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalCacheService implements ICacheService {
	
	private static int CDefExpire = 1800;
	
	public class LocalCacheObject {
		private long m_Time;
		private Object m_object;
		private int m_expire = -1;
	
		public LocalCacheObject(Object obj) {
			this(obj, CDefExpire);
		}
		
		public LocalCacheObject(Object obj, int expire) {
			m_Time = new Date().getTime();
			m_object = obj;
			m_expire = expire;
		}		
		
		public boolean isExpire() {
			if (m_expire == -1) {
				return false;
			}			
			
			int nDeltaTime = (int)((new Date().getTime() - m_Time) / 1000);
			return nDeltaTime > m_expire;			
		}
		
		public Object getObject() {
			return m_object;					
		}		
	}
	
	private ConcurrentMap<String, LocalCacheObject> m_Map = new ConcurrentHashMap<String, LocalCacheObject>();

	@Override
	public <T> boolean set(String key, T value) {
		LocalCacheObject obj = new LocalCacheObject(value);
		return (m_Map.put(key, obj) != null);
	}

	@Override
	public <T> boolean set(String key, T value, int expire) {
		LocalCacheObject obj = new LocalCacheObject(value, expire);
		return (m_Map.put(key, obj) != null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		LocalCacheObject obj = m_Map.get(key);
		if (obj != null) {
			if (obj.isExpire()) {
				m_Map.remove(key);
			} else {
				return (T) obj.getObject();
			}
		}
		
		return null;
	}
	
	/**
     * @description 根据key获取存储的值(抛异常时返回null)
     * @param key
     * @return <T>
     * @author tony
     * @created 2016-05-09
     */
	@Override
	public <T> T getByKey(String key){
		try{
			LocalCacheObject obj = m_Map.get(key);
			if (obj != null) {
				if (obj.isExpire()) {
					m_Map.remove(key);
				} else {
					return (T) obj.getObject();
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@Override
	public boolean delete(String key) {
		m_Map.remove(key);
		return true;
	}

	@Override
	public boolean exist(String key) {
		return m_Map.containsKey(key);
	}

	@Override
	public <T> boolean setn(String key, T value) {
		if(m_Map.containsKey(key)){
			LocalCacheObject oldCache = m_Map.get(key);
			if(!oldCache.isExpire()){
				return false;
			}
		}
		LocalCacheObject obj = new LocalCacheObject(value);
		return (m_Map.put(key, obj) != null);
	}

	@Override
	public <T> boolean setn(String key, T value, int expire) {
		if(m_Map.containsKey(key)){
			LocalCacheObject oldCache = m_Map.get(key);
			if(!oldCache.isExpire()){
				return false;
			}
		}
		LocalCacheObject obj = new LocalCacheObject(value,expire);
		return (m_Map.put(key, obj) != null);
	}
	/**
	 * 原子性设置key-value
	 * @param key
	 * @param value
	 * @param expire
	 * @return
	 */
	public <T> boolean cas(String key, T value, int expire){
		String casKey = "cas_"+key;
		if(m_Map.containsKey(key)){
			LocalCacheObject casObj = m_Map.get(casKey);
			if(!casObj.isExpire()){
				long oldCas = (long) casObj.getObject();
				synchronized (key) {
					LocalCacheObject newObj = m_Map.get(casKey);
					long newCas = (long) newObj.getObject();
					if(newCas!=oldCas){
						return false;
					}
				}
			}
		}
		synchronized (key) {
			long cas = System.currentTimeMillis();
			LocalCacheObject  casObj = new LocalCacheObject(cas,0);
			m_Map.put(casKey, casObj);
			LocalCacheObject obj = new LocalCacheObject(value,expire);
			m_Map.put(key, obj);
		}
		return true;
	}
	
	/**
	 * 获取key的版本号
	 * @param key
	 * @return
	 */
	public long getVersion(String key){
		return 0;
	}
	/**
	 * 是否shutdown
	 * @return
	 */
	public boolean isShutdown(){
		return false;
	}
	
	/**
     * 添加到List
     * @param key
     * @param value
     * @return
     */
    public boolean addList(String key, String... value){
    	return false;
    }
	
	/**
     * 检查List长度
     * @param key
     * @return
     */
    public long countList(String key){
    	return 0;
    }
	
	
	/**
     * 获取List
     * @param key
     * @return
     */
    public  List<String> getList(String key){
    	return null;
    }
	
	/**
     * 截取List
     * @param key 
     * @param start 起始位置
     * @param end 结束位置
     * @return
     */
    public List<String> rangeList(String key, long start, long end){
    	return null;
    }
    
    /**
     * 修剪List内存
     * @param key
     * @param start
     * @param end
     */
    public void lTrimList(String key, long start, long end){
    }

	@Override
	public boolean addSet(String key, String... value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeFromSet(String key, String... value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> getSet(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public boolean rPush(String key, String... value) {
    	return false;
    }
    
    public String lPop(String key) {
    	return null;
    }		
	
}
