package org.spring.springboot.utils;

import org.apache.ibatis.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * @author luckylau
 * @date 2017/12/13/013 10:15
 */
public class RedisClient {
    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private JedisTemplate jedisTemplate;

    /**
     * 降级
     */
    public static interface Loader<K, V> {
        public V load(K key) throws CacheException;
    }

    public static <K, V> V get(JedisTemplate jedisTemplate, K key, Loader<K, V> loader) {
        return get(jedisTemplate, key, loader, null, null);
    }

    public static <K, V> V get(JedisTemplate jedisTemplate, K key, Loader<K, V> loader, Long timeout, TimeUnit unit){
        V value;
        Jedis jedis = jedisTemplate.initJedisCilent();
        RedisSerializer keySlz = jedisTemplate.getKeySerializer();
        RedisSerializer valueSlz = jedisTemplate.getValueSerializer();

        try {
            byte[] rawKey = keySlz.serialize(key);
            byte[] rawValue = jedis.get(rawKey);
            value =(V)valueSlz.deserialize(rawValue);
        } catch (Exception e) {
            logger.error("get key: " + key + " cache exception : " + e.getMessage(), e);
            value = null;
        }


        if(value == null && loader != null){
            try {
                value = loader.load(key);
            } catch (CacheException e) {
                logger.error("Load cache failed: " + e.getMessage(), e);
            }

            try {
                if(value != null){
                    byte[] rawKey = keySlz.serialize(key);
                    byte[] rawValue = valueSlz.serialize(value);
                    jedis.set(rawKey, rawValue);
                    if(timeout != null && unit != null){
                        jedis.expireAt(rawKey, unit.toMillis(timeout));
                    }
                }
            } catch (SerializationException e) {
                logger.error(e.getMessage(), e);
            }
        }

        jedis.close();

        return value;
    }

    public static <K, V> void set(JedisTemplate jedisTemplate, K key, V value){
        set(jedisTemplate, key, value, null, null);
    }

    public static <K, V> void set(JedisTemplate jedisTemplate, K key, V value, Long timeout, TimeUnit unit){
        Jedis jedis = jedisTemplate.initJedisCilent();
        RedisSerializer keySlz = jedisTemplate.getKeySerializer();
        RedisSerializer valueSlz = jedisTemplate.getValueSerializer();

        try {
            byte[] rawKey = keySlz.serialize(key);
            byte[] rawValue = valueSlz.serialize(value);
            jedis.set(rawKey, rawValue);
            if(timeout != null && unit != null){
                jedis.pexpire(rawKey, unit.toMillis(timeout));
            }
        } catch (SerializationException e) {
            logger.error(e.getMessage(), e);
        }




    }









}
