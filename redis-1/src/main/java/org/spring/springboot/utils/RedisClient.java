package org.spring.springboot.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author luckylau
 * @date 2018/2/2/002 16:29
 */
public class RedisClient {
    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    public static interface Loader<K, V> {
        /**
         * 不允许返回null
         *
         * @param key
         * @return 不允许返回null
         * @throws CacheException
         */
        public V load(K key) throws CacheException;
    }

    public static class CacheException extends Exception {

        public CacheException() {
        }

        public CacheException(String message) {
            super(message);
        }

        public CacheException(String message, Throwable cause) {
            super(message, cause);
        }

        public CacheException(Throwable cause) {
            super(cause);
        }
    }

    public static <K, V> V get(RedisTemplate<K, V> redisTemplate, K key, Loader<K, V> loader) {
        return get(redisTemplate, key, loader, null, null);
    }

    /**
     * 从Redis中get数据，如果miss则用Loader load数据并写入缓存
     *
     * @param redisTemplate
     * @param key
     * @param loader
     * @param <K>
     * @param <V>
     * @return
     * @throws CacheException
     */
    public static <K, V> V get(RedisTemplate<K, V> redisTemplate, K key, Loader<K, V> loader, Long timeout, TimeUnit unit) {
        V value;
        try {
            value = redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("get key: " + key + " cache exception : " + e.getMessage(), e);
            value = null;
        }

        if (value == null && loader != null) {
            try {
                value = loader.load(key);
            } catch (CacheException e) {
                logger.error("Load cache failed: " + e.getMessage(), e);
            } catch (Exception e) {
                logger.error("error rey is..." + key);
                logger.error(e.getMessage(), e);
            }
            try {
                if (value != null) {
                    if (timeout == null || unit == null) {
                        redisTemplate.opsForValue().set(key, value);
                    } else {
                        redisTemplate.opsForValue().set(key, value, timeout, unit);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return value;
    }

    /**
     * mget接口
     * @param redisTemplate
     * @param keys
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> List<V> mget(RedisTemplate<K, V> redisTemplate, List<K> keys) {
        List<V> value;
        try {
            value = redisTemplate.opsForValue().multiGet(keys);
        } catch (Exception e) {
            logger.error("get keys: {} cache exception : " + e.getMessage(), keys, e);
            value = null;
        }
        return value;
    }

    public static <K, V> void mset(RedisTemplate<K, V> redisTemplate, Map<K, V> map) {
        try {
            redisTemplate.opsForValue().multiSet(map);
        } catch (Exception e) {
            logger.error("set map keys: {} cache exception : " + e.getMessage(), JSON.toJSONString(map.keySet()), e);
        }
    }

    /*
    * 遍历map实现mset，这样保证过期时间一定能加上
    * */
    public static <K, V> void mset(RedisTemplate<K, V> redisTemplate, Map<K, V> map, Long timeout, TimeUnit unit) {
        for(K key : map.keySet()){
            set(redisTemplate, key, map.get(key), timeout, unit);
        }
    }

    public static <K, V> void pipelineMset(final RedisTemplate<K, V> redisTemplate, final Map<K, V> map, final Long timeout, final TimeUnit unit) {
        //首先调用mset
        mset(redisTemplate, map);
        //然后用管道方式批量设置超时
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                for(K key:map.keySet()){
                    RedisSerializer keySlz = redisTemplate.getKeySerializer();
                    byte[] rawKey = keySlz.serialize(key);
                    redisConnection.pExpire(rawKey, TimeoutUtils.toMillis(timeout,unit));
                }
                return null;
            }
        });
    }

    public static <K, V> void msetIfAbsent(RedisTemplate<K, V> redisTemplate, Map<K, V> map) {
        try {
            redisTemplate.opsForValue().multiSetIfAbsent(map);
        } catch (Exception e) {
            logger.error("set map keys: {} cache exception : " + e.getMessage(), JSON.toJSONString(map.keySet()), e);
        }
    }

    /**
     * 从Redis中get数据，如果miss则用Loader load数据并写入缓存
     *
     * @param redisTemplate
     * @param key
     * @param <K>
     * @param <V>
     * @return
     * @throws CacheException
     */
    public static <K, V> V get(RedisTemplate<K, V> redisTemplate, K key) {
        return get(redisTemplate, key, null);
    }

    public static <K, V> void set(RedisTemplate<K, V> redisTemplate, K key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K, V> void set(RedisTemplate<K, V> redisTemplate, K key, V value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K, V> boolean setIfAbsent(RedisTemplate<K, V> redisTemplate, K key, V value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static <K, V> void delete(RedisTemplate<K, V> redisTemplate, K key) {
        for (int i = 0; i < 3; i++) {
            try {
                redisTemplate.delete(key);
                return;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static <K, V> void delete(RedisTemplate<K, V> redisTemplate, Collection<K> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K, V> Long increment(RedisTemplate<K, V> redisTemplate, K key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K, V> Double increment(RedisTemplate<K, V> redisTemplate, K key, double delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K, V> Boolean hasKey(RedisTemplate<K, V> redisTemplate, K key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    public static <K, V> void expire(RedisTemplate<K, V> redisTemplate, K key, long timeout, TimeUnit unit) {
        try {
            redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K, V> Long getExpire(RedisTemplate<K, V> redisTemplate, K key, TimeUnit unit) {
        try {
            return redisTemplate.getExpire(key, unit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K, V> void rightPush(RedisTemplate<K, V> redisTemplate, K key, V value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K, V> void leftPush(RedisTemplate<K, V> redisTemplate, K key, V value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K, V> V index(RedisTemplate<K, V> redisTemplate, K key, long index){
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K, V> void trim(RedisTemplate<K, V> redisTemplate, K key, long start, long end){
        try {
            redisTemplate.opsForList().trim(key, start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K, V> Long size(RedisTemplate<K, V> redisTemplate, K key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0L;
        }
    }

    public static <K, V> void rightPushAll(RedisTemplate<K, V> redisTemplate, K key, V... values) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K, V> List<V> range(RedisTemplate<K, V> redisTemplate, K key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ImmutableList.of();
        }
    }

    public static <K, V> Long sadd(RedisTemplate<K, V> redisTemplate, K key, V... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K, V> Set<V> smembers(RedisTemplate<K, V> redisTemplate, K key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K, V> Long scard(RedisTemplate<K, V> redisTemplate, K key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K, V> Set<V> zrange(RedisTemplate<K, V> redisTemplate, K key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ImmutableSet.of();
        }
    }

    public static <K, V> Set<ZSetOperations.TypedTuple<V>> zrangeWithScores(RedisTemplate<K, V> redisTemplate, K key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ImmutableSet.of();
        }
    }

    public static <K, V> Set<ZSetOperations.TypedTuple<V>> reverseRangeWithScores(RedisTemplate<K, V> redisTemplate, K key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ImmutableSet.of();
        }
    }

    public static <K,V> void zadd(RedisTemplate<K, V> redisTemplate,final K key, final List<V> values){
        try{
            redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    for (int i = 0; i < values.size(); i++) {
                        redisConnection.zAdd(key.toString().getBytes(),Double.parseDouble(i+""),values.get(i).toString().getBytes());
                    }
                    return null;
                }
            });
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    public static <K,V> void zadd(RedisTemplate<K, V> redisTemplate,final K key, final Set<ZSetOperations.TypedTuple<V>> tuples){
        try{
            redisTemplate.opsForZSet().add(key, tuples);
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * add by sunqiangming, 增加sorted set相关函数接口
     */
    public static <K,V> void zadd(RedisTemplate<K, V> redisTemplate, K key, double score,  V value){
        try {
            redisTemplate.opsForZSet().add(key,value, score);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    public static <K,V> Long zcount(RedisTemplate<K, V> redisTemplate, K key, double min, double max){
        try {
            return redisTemplate.opsForZSet().count(key, min, max);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    public static <K,V> Long zsize(RedisTemplate<K, V> redisTemplate, K key){
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K,V> void zremove(RedisTemplate<K, V> redisTemplate, K key, V value){
        try {
            redisTemplate.opsForZSet().remove(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K,V> void zremoveRange(RedisTemplate<K, V> redisTemplate, K key, long start, long end){
        try {
            redisTemplate.opsForZSet().removeRange(key, start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K,V> void zRemoveRangeByScore(RedisTemplate<K, V> redisTemplate, K key, double min, double max){
        try {
            redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <K,V> Map<V, Long> mZRank(RedisTemplate<K, V> redisTemplate, K k, List<V> values){
        Map<V, Long> map = new HashMap<>();

        for(V v : values){
            map.put(v, zRank(redisTemplate, k, v));
        }

        return map;
    }

    public static <K,V> Long zRank(RedisTemplate<K, V> redisTemplate, K key, V v){
        try {
            return redisTemplate.opsForZSet().rank(key, v);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            return null;
        }
    }

    public static <K,V> Map<V, Double> mZScore(RedisTemplate<K, V> redisTemplate, K key, List<V> values){
        Map<V, Double> map = new HashMap<>();

        for(V v : values){
            map.put(v, zScore(redisTemplate, key, v));
        }

        return map;
    }

    public static <K,V> Double zScore(RedisTemplate<K, V> redisTemplate, K key, V v){
        try {
            return redisTemplate.opsForZSet().score(key, v);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            return null;
        }
    }

    public static <K,V> void hsetAll(RedisTemplate<K, V> redisTemplate,K key,Map<K,V> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    public static <K,V> void hset(RedisTemplate<K, V> redisTemplate,K key,Object hk,Object hv){
        try {
            redisTemplate.opsForHash().put(key, hk, hv);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    public static <K,V> V hget(RedisTemplate<K, V> redisTemplate,K key,Object hk){
        try {
            return ((HashOperations<K, K, V>)redisTemplate.opsForHash()).get(key, hk);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K,V> List<V> hmultiGet(RedisTemplate<K, V> redisTemplate,K key, Collection<K> hashKeys){
        try {
            return ((HashOperations<K, K, V>)redisTemplate.opsForHash()).multiGet(key, hashKeys);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K, V> Map hgetAll(RedisTemplate<K, V> redisTemplate, K key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ImmutableMap.of();
        }
    }

    public static <K, HK, V> Long hincrement(RedisTemplate<K, V> redisTemplate, K key, HK hkey, long delta) {
        try {
            return redisTemplate.opsForHash().increment(key, hkey, delta);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <K, HK, V> Double hincrement(RedisTemplate<K, V> redisTemplate, K key, HK hkey, double delta) {
        try {
            return redisTemplate.opsForHash().increment(key, hkey, delta);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static boolean isCountLimit(RedisTemplate<String, Long> redisTemplate, String key, int count, Long timeout, TimeUnit unit){
        if(count < 1){
            return true;
        }

        key = "limit_" + key;
        leftPush(redisTemplate, key, System.currentTimeMillis());
        expire(redisTemplate, key, timeout, unit);
        /*
        * 如果次数小于规定的，说明没超过限制
        * */
        if(size(redisTemplate, key) < count){
            return false;
        }

        Long leftTime = index(redisTemplate, key, count -1);
        if(leftTime == null){
            return false;
        }

        trim(redisTemplate, key, 0, count - 1);

        /*
        * 最早一条如果不在时间内，说明没问题
        * */
        return (System.currentTimeMillis() - leftTime) < unit.toMillis(timeout);
    }
}
