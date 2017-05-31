package com.glodon.common.cache;


import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;



public class CacheServiceConfig {
	public enum CacheType {
		local, memcache, redis
	}

	public static class CacheConfig {

		private String name;
		private CacheType type;
		private String server;
		private Integer poolSize;
		private Integer defExpire;
	

		public CacheConfig(String name, String type, String server,
				Integer poolSize, Integer defExpire) {
			this.name = name;
			this.type = getTypeFromStr(type);
			this.server = server;
			this.poolSize = poolSize;
			this.defExpire = defExpire;
		}

	

		private CacheType getTypeFromStr(String type) {
			if ((type == null) || (type.trim().isEmpty())) {
				return CacheType.local;
			}

			if (type.equals("memcache")) {
				return CacheType.memcache;
			} else if (type.equals("redis")) {
				return CacheType.redis;
			} else {
				return CacheType.local;
			}
		}

		public String getName() {
			return name;
		}

		public CacheType getType() {
			return type;
		}

		public String getServer() {
			return server;
		}

		public Integer getPoolSize() {
			return poolSize;
		}

		public Integer getDefExpire() {
			return defExpire;
		}


	}

	private static ResourceBundle m_bundle = ResourceBundle.getBundle("Cache");
	private static  ConcurrentMap<String,String> v_map = new ConcurrentHashMap<String,String>();

	/**
	 * 获取指定的缓存配置
	 * 
	 * @param cacheName
	 * @return
	 */
	public static CacheConfig getCacheConfig(String cacheName) {
		return initParam(cacheName);
	}



	/**
	 * 初始化缓存参数
	 * 
	 * @param cacheName
	 * @return
	 */
	private static CacheConfig initParam(String cacheName) {
		if (null == m_bundle) {
			throw new NullPointerException("缓存配置文件为空");
		}
		try {
			  Set<String> keySet = m_bundle.keySet();
			  	for (String key : keySet) {
			  		v_map.put(key, m_bundle.getString(key));
				}
			String sCacheType =v_map.get(cacheName + ".type");
			String sCacheExpires = v_map.get(cacheName + ".expires"); 
			String sServer = v_map.get(cacheName + ".server"); 
			String sPoolSize = v_map.get(cacheName + ".connectionPoolSize"); 
			
			Integer poolSize = Integer.parseInt(sPoolSize);
			Integer nDefExpire = Integer.parseInt(sCacheExpires);
			return new CacheConfig(cacheName, sCacheType, sServer, poolSize,nDefExpire);
		} catch (Exception e) {
			throw new RuntimeException("缓存配置文件有误",e);
		}
	}

	/**
	 * 对外暴露map的内容
	 * @param key
	 * @return
	 */
	public static String getValue(String key){
		return v_map.get(key);
	}
}
