package com.glodon.common.cache;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.log4j.Logger;

import com.glodon.common.cache.CacheServiceConfig.CacheConfig;

public class MemcacheCacheService implements ICacheService {
	private static Logger log = Logger.getLogger(CacheServiceConfig.class);
	private MemcachedClientBuilder builder = null;
	private MemcachedClient client=null;
	//默认过期时间(单位：秒)
	private Integer default_expire;
	 
	public MemcacheCacheService(CacheConfig config) {
		builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(config.getServer()));
		try {
			client = builder.build();
			default_expire = (null != config.getDefExpire()) ? config.getDefExpire() : 1800;
		} catch (IOException e) {
			log.error("初始化MemcacheCacheService失败：",e);
		}
	}

	@Override
	public <T> boolean set(String key, T value) {
		return set(key, value, default_expire);
	}

	@Override
	public <T> boolean set(String key, T value, int expire) {
		try {
			if (validateKey(key)) {
				return client.set(key, expire, value);
			}
		} catch (Exception e) {
			log.error("方法[set]设置缓存["+key+"]失败：",e);
		}
		return false;
	}
	
	@Override
	public <T> boolean setn(String key, T value) {
		try {
			if (validateKey(key)) {
				return client.add(key, 0,value);
			}
		} catch (Exception e) {
			log.error("方法[setn0]设置缓存["+key+"]失败：",e);
		}
		return false;
	}

	@Override
	public <T> boolean setn(String key, T value, int expire) {
		try {
			if (validateKey(key)) {
				return client.add(key, expire,value);
			}
		} catch (Exception e) {
			log.error("方法[setn1]设置缓存["+key+"]失败：",e);
		}
		return false;
	}
	/**
	 * 可与cachelock配合使用
	 * 原子性设置key-value
	 * @param key
	 * @param value
	 * @param expire
	 * @return
	 */
	public <T> boolean cas(String key, T value, int expire){
		try{
			if(validateKey(key)){
				GetsResponse<T>  result = client.gets(key);
				if(null != result){
					long cas = result.getCas();
					return  client.cas(key, expire,value,cas);
				}else{
					return client.set(key, expire, value);
				}
			}
		}catch(Exception e){
			log.error("方法[cas]设置缓存["+key+"]失败：",e);
		}
		return false;
	}
	@Override
	public <T> T get(String key) {
		try {
			if (validateKey(key)) {
				return client.get(key);
			}
		} catch (Exception e) {
			log.error("方法[get]获取缓存["+key+"]失败：",e);
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
		try {
			if (validateKey(key)) {
				return client.get(key);
			}
		} catch (Exception e) {
			log.error("方法[get]获取缓存["+key+"]失败：",e);
			return null;
		}
		return null;
	}
	
	/**
	 * 获取key的版本号
	 * @param key
	 * @return
	 */
	public long getVersion(String key){
		long version = 0;
		try{
			if(validateKey(key)){
				GetsResponse<?>  result = client.gets(key);
				if(null != result){
					version = result.getCas();
				}
			}
		}catch(Exception e){
			log.error("方法[getVersion]获取缓存["+key+"]的版本号失败：",e);
		}
		return version;
	}
	@Override
	public boolean delete(String key) {
		try {
			if (validateKey(key)) {
				return client.delete(key);
			}
		} catch (Exception e) {
			log.error("方法[delete]删除缓存["+key+"]失败：",e);
		}
		return false;
	}

	@Override
	public boolean exist(String key) {
		try {
			if (validateKey(key)) {
				return client.get(key) != null;
			}
		} catch (Exception e) {
			log.error("方法[exist]判断缓存["+key+"]是否存在失败：",e);
		}
		return false;
	}
	/**
	 * 检查
	 * @param key
	 * @return
	 */
	private boolean validateKey(String key){
		if (key != null && key.trim().length() > 0  && client != null) {
			return true;
		}
		return false;
	}
	/**
	 * 是否shutdown
	 * @return
	 */
	public boolean isShutdown(){
		return client == null ? true : client.isShutdown();
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
