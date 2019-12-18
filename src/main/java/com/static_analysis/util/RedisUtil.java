package com.static_analysis.util;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
/**
 * redicache 工具类
 * 如果有String类型需要转为字节码getBytes("ISO8859-1")才能存入否早出现乱码
 * 
 */
@SuppressWarnings("unchecked")
@Component
public class RedisUtil {
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	protected final static Logger log = LoggerFactory.getLogger(RedisUtil.class);
	/**
	 * 批量删除对应的value
	 * 
	 * @param keys
	 */
	public void remove(final String... keys) {
		for (String key : keys) {
		remove(key);
		}
	}
	public Set<Serializable> findPattern(final String pattern){
		return redisTemplate.keys(pattern);
	}
	/**
	 * 批量删除key
	 * 
	 * @param pattern
	 */
	public void removePattern(final String pattern) {
		Set<Serializable> keys = redisTemplate.keys(pattern);
		if (keys.size() > 0)
		redisTemplate.delete(keys);
	}
	/**
	 * 删除对应的value
	 * 
	 * @param key
	 */
	public void remove(final String key) {
		if (exists(key)) {
		redisTemplate.delete(key);
		}
	}
	/**
	 * 判断缓存中是否有对应的value
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}
	/**
	 * 读取缓存
	 * 
	 * @param key
	 * @return
	 */
	public String get(final String key) {
		Object result = null;
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		if(result==null){
			return null;
		}
		return result.toString();
	}
	public Object getOriginal(final String key) {
		Object result = null;
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		if(result==null){
			return null;
		}
		return result;
	}
	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, Object value) {
		boolean result = false;
		try {
		if(value.getClass().getName()=="java.lang.String"){
		value =new String(value.toString().getBytes("utf-8"),"utf-8");
		}
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		operations.set(key, value);
		result = true;
		} catch (Exception e) {
		e.printStackTrace();
		}
		return result;
	}
	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value,如果是String类型需要转为字节码getBytes("ISO8859-1")
	 * @return
	 */
	public boolean set(final String key, Object value, Long expireTime) {
		boolean result = false;
		try {
		if(value.getClass().getName()=="java.lang.String"){
		value =new String(value.toString().getBytes("ISO8859-1"));
	}
	ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
	operations.set(key, value);
	redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
	result = true;
	} catch (Exception e) {
	e.printStackTrace();
	}
	return result;
	}
	/**
	 * 存入Set值
	 * @param key
	 * @param set
	 * @return
	 */
	public boolean setSet(final String key,Set<String> set){
		boolean result = false;
		try {
			redisTemplate.opsForSet().add(key, set);
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 获得Set值
	 * @param key
	 * @return
	 */
	public Set<String> getSet(final String key){
		try {
			Set<String> resultSet = redisTemplate.opsForSet().members(key);
			return resultSet;
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	/**
	 * 存入Map对象
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setMap(String key,Map<String,Set<String>> value){
		boolean result = false;
		try {
			redisTemplate.opsForHash().putAll(key, value);
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 获得Map对象
	 * @param key
	 * @return
	 */
	public Map<String,Set<String>> getMap(String key){
		try {
			return redisTemplate.opsForHash().entries(key);
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	public  boolean hmset(String key, Map<String, String> value) {
		boolean result = false;
		try {
			redisTemplate.opsForHash().putAll(key, value);
			result = true;
		} catch (Exception e) {
		e.printStackTrace();
		}
		return result;
	}
	
	public  Map<String,String> hmget(String key) {
		Map<String,String> result =null;
		try {
			result=  redisTemplate.opsForHash().entries(key);
		} catch (Exception e) {
		e.printStackTrace();
		}
		return result;
	}
}
