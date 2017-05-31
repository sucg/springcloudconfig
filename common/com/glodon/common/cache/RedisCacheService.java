package com.glodon.common.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

import com.glodon.common.cache.CacheServiceConfig.CacheConfig;

public class RedisCacheService implements ICacheService {
	private static Logger log = Logger.getLogger(CacheServiceConfig.class);
	private JedisPool oPool = null;
	private String sHost = null;
	private String sPort = null;
	//默认过期时间(单位：秒)
	private int default_expire;
	
	public RedisCacheService(CacheConfig config) {
		if (null == oPool) {
			String sMaxTotalV = CacheServiceConfig.getValue(config.getName()+ ".maxTotal");
			String sMaxIdleV = CacheServiceConfig.getValue(config.getName()+ ".maxIdle");
			String sMaxWaitV = CacheServiceConfig.getValue(config.getName()+ ".maxWait");
			String sExpiresV = CacheServiceConfig.getValue(config.getName()+ ".expires");
			
			String sMaxTotal = (sMaxTotalV == null) ? "0" : sMaxTotalV;
			String sMaxIdle = (sMaxIdleV == null) ? "0" : sMaxIdleV;
			String sMaxWait = (sMaxWaitV == null) ? "0" : sMaxWaitV;
			//默认过期时间一个星期
			default_expire = (sExpiresV == null) ? 7*24*60*60 : Integer.parseInt(sExpiresV);
			
			String servreAddress = config.getServer();
			sHost = servreAddress.split(":")[0];
			sPort = servreAddress.split(":")[1];
			JedisPoolConfig JedisConfig = new JedisPoolConfig();
			JedisConfig.setMaxTotal(Integer.valueOf(sMaxTotal));
			JedisConfig.setMaxIdle(Integer.valueOf(sMaxIdle));
			JedisConfig.setMaxWaitMillis(Integer.valueOf(sMaxWait));
			JedisConfig.setTestOnBorrow(true);
			JedisConfig.setBlockWhenExhausted(true);
			oPool = new JedisPool(JedisConfig, sHost, Integer.parseInt(sPort));
		}
	} 
	private Jedis getJedis() {
		Jedis jedis = null;
		try {
			jedis = oPool.getResource();
		} catch (JedisException e) {
			log.error("获取 Jedis 实例失败", e);
		}
		return jedis;
	}

	@Override
	public <T> boolean set(String key, T bValue) {
		return set(key, bValue, default_expire);
	}

	@Override
	public <T> boolean set(String key, T bValue, int expire) {
		boolean isBorken = false;
		Jedis jedis = getJedis();
		try {
			if (null != jedis) {
				byte[] objArry = SerializationUtils.serialize((Serializable) bValue);
				String setex = jedis.setex(key.getBytes(), expire, objArry);
				if (setex.equalsIgnoreCase("OK")) {
					return true;
				} else {
					return false;
				}

			}
		} catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 setex 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}

		return false;
	}

	/**
	 * 如果该key已经存在，则不进行任何操作 如果该key不存在，则不进行新增操作 返回false
	 */
	@Override
	public <T> boolean setn(String key, T bValue) {
		boolean isBorken = false;
		Jedis jedis = getJedis();
		try {
			if (null != jedis) {
				byte[] objArry = SerializationUtils.serialize((Serializable) bValue);
				Long returnInfo = jedis.setnx(key.getBytes(), objArry);
				if (returnInfo == 1) {
					return true;
				} else {
					return false;
				}
			}
		} catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 setn 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}

		return false;
	}

	/**
	 * 如果该key已经存在，则不进行任何操作 如果该key不存在，则不进行新增操作 返回false
	 */
	@Override
	public <T> boolean setn(String key, T bValue, int expire) {
		boolean isBorken = false;
		Jedis jedis = getJedis();
		try {
			if (null != jedis) {
				byte[] objArry = SerializationUtils.serialize((Serializable) bValue);
				Long returnInfo = jedis.setnx(key.getBytes(), objArry);
				if (returnInfo == 1) {
					jedis.expire(key, expire);
					return true;
				} else {
					return false;
				}
			}
		} catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 setnx 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}

		return false;
	}

	/**
	 * 使用redis的事务进行cas操作: MULTI 执行之后，客户端可以继续向服务器发送任意多条命令，这些命令不会立即被执行，而是被放到一个队列中
	 * EXEC 命令被调用时，所有队列中的命令才会被执行。 如果调用exec时，该key对应的bValue发生改变，前面操作一律失效
	 */
	@Override
	public <T> boolean cas(String key, T bValue, int expire) {
		boolean isBorken = false;
		Jedis jedis = getJedis();
		try {
			if (null != jedis) {
				byte[] objArry = SerializationUtils.serialize((Serializable) bValue);
				jedis.watch(key);// 监听该key
				Transaction t = jedis.multi();// 开启对应的事务
				t.setex(key.getBytes(), expire, objArry);
				List<Object> result = t.exec();// 执行事务操作队列
				if (null == result || result.isEmpty()) {
					log.error(" Redis 事务执行失败" + key);
					return false;
				}
				return true;
			}
		} catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 cas 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}

		return false;

	}

	@Override
	public <T> T get(String key) {
		boolean isBorken = false;
		byte[] bValue = null;
		Jedis jedis = getJedis();
		try {
			if (null != jedis) {
				bValue = jedis.get(key.getBytes());
				if (null == bValue) {
					return null;
				}
				return SerializationUtils.deserialize(bValue);
			}
		} catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 get 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
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
		boolean isBorken = false;
		byte[] bValue = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			if (null != jedis) {
				bValue = jedis.get(key.getBytes());
				if (null == bValue) {
					return null;
				}
				return SerializationUtils.deserialize(bValue);
			}
		} catch (JedisException e) {
			try{
				isBorken = handleJedisException(e);
			} catch (Exception en) {
				en.printStackTrace();
				log.error("[Redis] handleJedisException 失败 key->" + key, en);
			}
			log.error("[Redis] 调用 get 方法失败 key->" + key, e);
			return null;
		} finally {
			closeResource(jedis, isBorken);
		}
		return null;
	}

	@Override
	public boolean delete(String key) {
		boolean isBorken = false;
		Jedis jedis = getJedis();
		try {
			if (null != jedis) {
				Long returnInfo = jedis.del(key.getBytes());
				if (returnInfo == 1) {
					return true;
				} else {
					return false;
				}
			}
		} catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 delete 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
		return false;
	}

	@Override
	public boolean exist(String key) {
		boolean isBorken = false;
		boolean exists = false;
		Jedis jedis = getJedis();
		try {
			if (null != jedis) {
				exists = jedis.exists(key.getBytes());
			}
		} catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 exist 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
		return exists;

	}
	
	/**
     * 添加到List
     * @param key
     * @param value
     * @return
     */
    public boolean addList(String key, String... value) {
    	boolean isBorken = false;
    	Jedis jedis = getJedis();
        try {
        	if (null != jedis) {
        		long optSize = jedis.lpush(key, value);
        		if(0 != optSize ){
        			return true;
        		}
			}
        } catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 addList 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
        return false;
    }
	/**
	 * 添加到set
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean addSet(String key, String... value) {
    	boolean isBorken = false;
    	Jedis jedis = getJedis();
        try {
        	if (null != jedis) {
        		long optSize = jedis.sadd(key, value);
        		if(0 != optSize ){
        			return true;
        		}
			}
        } catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 addSet 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
        return false;
    }
	/**
	 * 从SET中删除
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean removeFromSet(String key, String... value){
		boolean isBorken = false;
    	Jedis jedis = getJedis();
        try {
        	if (null != jedis) {
        		long optSize = jedis.srem(key, value);
        		if(0 != optSize ){
        			return true;
        		}
			}
        } catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 removeFromSet 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
        return false;
	}
	/**
	 * 获取set
	 * @param key
	 * @return
	 */
	public Set<String> getSet(String key){
		boolean isBorken = false;
		Jedis jedis = getJedis();
		Set<String> oSet = null; 
        try {
        	if (null != jedis) {
        		oSet = jedis.smembers(key);
			}
        } catch (JedisException e) {
        	isBorken = handleJedisException(e);
			log.error("[Redis] 调用 getSet 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
        return oSet;
	}
	/**
     * 检查List长度
     * @param key
     * @return
     */
    public long countList(String key){
    	if(key == null ){
    		return 0;
    	}
    	boolean isBorken = false;
    	Jedis jedis = getJedis();
        try {
        	if (null != jedis) {
        		return jedis.llen(key);
			}
        } catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 countList 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
    	return 0;
    }
    
    /**
     * 修剪List内存
     * @param key
     * @param start
     * @param end
     */
    public void lTrimList(String key, long start, long end){
    	boolean isBorken = false;
    	Jedis jedis = getJedis();
        try {
        	if (null != jedis) {
        		jedis.ltrim(key, start, end);
			}
        } catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 lTrimList 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
    }
	
	
	/**
     * 获取List
     * @param key
     * @return
     */
    public  List<String> getList(String key){
    	boolean isBorken = false;
    	Jedis jedis = getJedis();
        try {
        	if (null != jedis) {
        		return jedis.lrange(key, 0, -1);
			}
        } catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 getList 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
        return new ArrayList<String>();
    }
	
	/**
     * 截取List
     * @param key 
     * @param start 起始位置
     * @param end 结束位置
     * @return
     */
    public List<String> rangeList(String key, long start, long end) {
        if (key == null || key.equals("")) {
            return null;
        }
        boolean isBorken = false;
    	Jedis jedis = getJedis();
        try {
        	if (null != jedis) {
        		return jedis.lrange(key, start, end);
			}
        } catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 rangeList 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
        return null;
    }
	
	

	@Override
	public boolean isShutdown() {
		Jedis jedis = getJedis();
		if (null == jedis || !jedis.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * redis暂不支持
	 */
	@Override
	public long getVersion(String key) {
		return 0;
	}

	protected void closeResource(Jedis jedis, boolean conectionBroken) {
		try {
			if (conectionBroken) {
				oPool.returnBrokenResource(jedis);
			} else {
				oPool.returnResource(jedis);
			}
		} catch (Exception e) {
			log.error("返回 Jedis实例失败", e);
		}
	}

	protected boolean handleJedisException(JedisException jedisException) {
		if (jedisException instanceof JedisConnectionException) {
			log.error("Redis 尝试连接 " + sHost + ":" + sPort + " 异常",jedisException);
		} else if (jedisException instanceof JedisDataException) {
			if ((jedisException.getMessage() != null)
					&& (jedisException.getMessage().indexOf("READONLY") != -1)) {
				log.error("Redis connection " + sHost + ":" + sPort+ " are read-only slave.", jedisException);
			} else {
				return false;
			}
		} else {
			log.error("Jedis 发生异常.", jedisException);
		}
		return true;
	}
	
    public boolean rPush(String key, String... value) {
    	boolean isBorken = false;
    	Jedis jedis = getJedis();
        try {
        	if (null != jedis) {
        		long optSize = jedis.rpush(key, value);
        		if(0 != optSize ){
        			return true;
        		}
			}
        } catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 addList 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
        return false;
    }
    
    public String lPop(String key) {
    	boolean isBorken = false;
    	Jedis jedis = getJedis();
        try {
        	if (null != jedis) {
        		return jedis.lpop(key);
			}
        } catch (JedisException e) {
			isBorken = handleJedisException(e);
			log.error("[Redis] 调用 getList 方法失败 key->" + key, e);
		} finally {
			closeResource(jedis, isBorken);
		}
        return null;
    }

}
