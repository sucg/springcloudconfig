package com.glodon.common.cache;

import java.util.List;
import java.util.Set;


public interface ICacheService {
	
	public <T> boolean set(String key, T value);

	public <T> boolean set(String key, T value, int expire);
	/**
	 * 如果key已经存在 则返回false 
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> boolean setn(String key, T value);
	/**
	 * 如果key已经存在 则返回false 
	 * @param key
	 * @param value
	 * @param expire 过期时间（单位：秒）
	 * @return
	 */
	public <T> boolean setn(String key, T value, int expire);
	/**
	 * 原子性设置key-value
	 * @param key
	 * @param value
	 * @param expire
	 * @return
	 */
	public <T> boolean cas(String key, T value, int expire);
	/**
	 * 根据key获取对象
	 * @param key
	 * @return
	 */
	public <T> T get(String key);
	/**
	 * 删除指定缓存
	 * @param key
	 * @return
	 */
	public boolean delete(String key);
	/**
	 * 判断指定缓存是否存在
	 * @param key
	 * @return
	 */
	public boolean exist(String key);
	/**
	 * 是否shutdown
	 * @return
	 */
	public boolean isShutdown();
	/**
	 * 获取key的版本号
	 * @param key
	 * @return
	 */
	public long getVersion(String key);
	
	/**
     * @description 根据key获取存储的值(抛异常时返回null)
     * @param key
     * @return <T>
     * @author tony
     * @created 2016-05-09
     */
	public <T> T getByKey(String key);
	/**
     * 添加到List
     * @param key
     * @param value
     * @return
     */
    public boolean addList(String key, String... value);
	
	/**
     * 检查List长度
     * @param key
     * @return
     */
    public long countList(String key);
	
	
	/**
     * 获取List
     * @param key
     * @return
     */
    public  List<String> getList(String key);
	
	/**
     * 截取List
     * @param key 
     * @param start 起始位置
     * @param end 结束位置
     * @return
     */
    public List<String> rangeList(String key, long start, long end);
    
    /**
     * 修剪List内存
     * @param key
     * @param start
     * @param end
     */
    public void lTrimList(String key, long start, long end); 
    
    /**
     * 添加到set
     * @param key
     * @param value
     * @return
     */
    public boolean addSet(String key, String... value);
    /**
     * 从set中删除
     * @param key
     * @param value
     * @return
     */
    public boolean removeFromSet(String key, String... value);
    /**
     * 获取集合
     * @param key
     * @return
     */
    public Set<String> getSet(String key);
    
    public boolean rPush(String key, String... value);
    
    public String lPop(String key);
}
