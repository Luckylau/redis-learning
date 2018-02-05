package org.spring.springboot.utils;

import org.apache.ibatis.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
                        jedis.pexpire(rawKey, unit.toMillis(timeout));
                    }
                }
            } catch (SerializationException e) {
                logger.error(e.getMessage(), e);
            }
        }

        if(jedis != null){
            jedis.close();
        }

        return value;
    }

    public static <K, V> List<V> mGet(JedisTemplate jedisTemplate, List<K> keys){
        List<V> values = new ArrayList<V>(keys.size());
        Jedis jedis = jedisTemplate.initJedisCilent();
        RedisSerializer keySerializer = jedisTemplate.getKeySerializer();
        RedisSerializer valueSerializer = jedisTemplate.getValueSerializer();
        final byte[][] rawKeys = new byte[keys.size()][];
        try {
            int counter = 0;
            for(K key : keys){
                byte[] rawKey = keySerializer.serialize(key);
                rawKeys[counter++] = rawKey;
            }
            List<byte[]> rawValues = jedis.mget(rawKeys);
            for(byte[] bs: rawValues){
                values.add((V)valueSerializer.deserialize(bs));
            }
        } catch (Exception e) {
            logger.error("mget keys: " + keys + " cache exception : " + e.getMessage(), e);
            return null;
        }

        if(jedis != null){
            jedis.close();
        }
        return values;
    }

    public static <K, V> void mSet(JedisTemplate jedisTemplate, Map<K, V> map, Long timeout, TimeUnit unit ) {
        Jedis jedis = jedisTemplate.initJedisCilent();
        Pipeline pipeline = jedis.pipelined();
        try {
            for (K key : map.keySet()) {
                RedisSerializer keySerializer = jedisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = jedisTemplate.getValueSerializer();
                byte[] rawKey = keySerializer.serialize(key);
                byte[] rawValue = valueSerializer.serialize(map.keySet());
                pipeline.set(rawKey, rawValue);
                pipeline.pexpire(rawKey, TimeoutUtils.toMillis(timeout, unit));
            }
            pipeline.sync();
        } catch (Exception e) {
            logger.error("set map keys: {} cache exception", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
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

        if(jedis != null){
            jedis.close();
        }

    }

    public static <K> void delete(JedisTemplate jedisTemplate, K key){
        Jedis jedis = jedisTemplate.initJedisCilent();
        RedisSerializer keySlz = jedisTemplate.getKeySerializer();
        byte[] rawKey = keySlz.serialize(key);
        jedis.del(rawKey);

        if(jedis != null){
            jedis.close();
        }
    }











}
