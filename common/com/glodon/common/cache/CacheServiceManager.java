package com.glodon.common.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.glodon.common.cache.CacheServiceConfig.CacheConfig;

public class CacheServiceManager {
	
	private static ConcurrentMap<String, ICacheService> m_CacheServiceMap = new ConcurrentHashMap<String, ICacheService>();
	/**
	 * 根据配置获取指定的缓存实例
	 * @param name
	 * @return
	 */
	private static ICacheService createCacheService(String name) {
		CacheConfig oConfig = CacheServiceConfig.getCacheConfig(name);
		return getCacheService(oConfig);
	}

	/**
	 * 根据配置获取对应的cache service对象实例
	 * @param oConfig
	 * @return
	 */
	private static ICacheService getCacheService(CacheConfig oConfig){
		if (oConfig != null) {
			switch (oConfig.getType()) {
			case memcache:
				return new MemcacheCacheService(oConfig);
			case redis:
				return new RedisCacheService(oConfig);
			default:
				return new LocalCacheService();
			}
		}
		return new LocalCacheService();
	}
	
	
	public static ICacheService getCacheServiceByName(String name) {
		ICacheService iService = m_CacheServiceMap.get(name);
		if (iService == null) {
			synchronized (CacheServiceManager.class) {
				iService = m_CacheServiceMap.get(name);
				if (iService == null) {
					iService = createCacheService(name);
					m_CacheServiceMap.put(name, iService);
				}
			}
		}
		return iService;
	}
	
}
