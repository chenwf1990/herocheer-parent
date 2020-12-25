package com.herocheer.cache.bean;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @desc redis公共方法
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
@Component("redisClient")
public class RedisClient {
    final static String HASH_KEY = "herocheer-sports:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置redis值
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置redis值，有过期时间
     *
     * @param key
     * @param value
     * @param expireTime
     */
    public void set(String key, String value, long expireTime) {
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取redis值
     *
     * @param key
     * @return
     */
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? "" : (String) value;
    }

    /**
     * 判断键值是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        final Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey == null ? false : hasKey;
    }

    /**
     * 删除key
     *
     * @auth chenwf
     * @date 2019/4/11
     */
    public void delete(String key) {
        this.redisTemplate.delete(key);
    }

    /**
     * 设置redis值
     *
     * @param key
     * @param value
     */
    public void hset(String key, String value) {
        redisTemplate.opsForHash().put(key,HASH_KEY,value);
    }

    /**
     * 获取redis值
     *
     * @param key
     * @return
     */
    public String hget(String key) {
        Object value = redisTemplate.opsForHash().get(key,HASH_KEY);
        return value == null ? "" : (String) value;
    }

    /**
     * 删除hash key
     *
     * @auth chenwf
     * @date 2019/4/11
     */
    public void deleteHkey(String key) {
        this.redisTemplate.opsForHash().delete(key,HASH_KEY);
    }

    /**
     * 判断hash键值是否存在
     *
     * @param key
     * @return
     */
    public boolean hasHkey(String key) {
        final Boolean hasKey = redisTemplate.opsForHash().hasKey(key,HASH_KEY);
        return hasKey == null ? false : hasKey;
    }
    /**
     * 增加锁防止重复跑
     *
     * @param key
     * @return
     */
    public boolean setnx(String key,String value,long expireTime) {
        return  redisTemplate.opsForValue().setIfAbsent(key,value,expireTime, TimeUnit.SECONDS);
    }

    /**
     * redis自减操作
     * @param key
     * @return
     */
    public Long decr(String key,long timeOut) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long decrement = entityIdCounter.decrementAndGet();
        entityIdCounter.expire(timeOut,TimeUnit.SECONDS);
        return decrement;
    }

    /**
     * 获取当前key的个数
     * @param key
     * @return
     */
    public Long getDecr(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return entityIdCounter.get();
    }

    /**
     * redis自增操作
     * @param key
     * @return
     */
    public Long incr(String key,long timeOut) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();
        entityIdCounter.expire(timeOut,TimeUnit.SECONDS);
        return increment;
    }

    /**
     * redis自增操作
     * @param key
     * @return
     */
    public Integer getIncr(String key) {
        try {
            RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
            return entityIdCounter.intValue();
        }catch (Exception e){

        }
        return 0;
    }

}
