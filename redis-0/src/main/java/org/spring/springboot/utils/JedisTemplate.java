package org.spring.springboot.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import redis.clients.jedis.Pipeline;
import redis.clients.util.Pool;
import redis.clients.jedis.Jedis;


/**
 * @author luckylau
 * @date 2017/12/15/015 14:15
 */
public class JedisTemplate implements InitializingBean {

    private JedisPoolFactory jedisPoolFactory;

    private Pool<Jedis> pool;

    private Jedis client;

    private RedisSerializer keySerializer = null;

    private RedisSerializer valueSerializer = null;

    private RedisSerializer<?> defaultSerializer;

    private ClassLoader classLoader;


    public Pipeline pipeline(){
        return client.pipelined();
    }

    public RedisSerializer getKeySerializer() {
        if(keySerializer == null ){
            keySerializer = defaultSerializer;
        }
        return keySerializer;
    }

    public void setKeySerializer(RedisSerializer keySerializer) {
        this.keySerializer = keySerializer;
    }

    public RedisSerializer getValueSerializer() {
        if(valueSerializer == null){
            valueSerializer = defaultSerializer;
        }
        return valueSerializer;
    }

    public void setValueSerializer(RedisSerializer valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    private JedisPoolFactory getJedisPoolFactory(){
        return jedisPoolFactory;
    }

    private void initJedisPool(){
        pool = jedisPoolFactory.createPool();

    }

    public Jedis initJedisCilent(){
        client = pool.getResource();
        return client;
    }

    public void closeJedisClient(){
        if(client != null){
            client.close();
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (defaultSerializer == null) {
            defaultSerializer = new JdkSerializationRedisSerializer(this.getClass().getClassLoader());
        }
        Assert.notNull(getJedisPoolFactory(), "jedisPoolFactory is required");
        initJedisPool();

    }

    public void setJedisPoolFactory(JedisPoolFactory jedisPoolFactory) {
        this.jedisPoolFactory = jedisPoolFactory;
    }
}


