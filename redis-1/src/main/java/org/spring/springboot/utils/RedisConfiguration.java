package org.spring.springboot.utils;

import org.spring.springboot.utils.serialize.KryoRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author liujun
 * @date 2018/2/1/001 19:54
 */
@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.database}")
    private int db;

    @Value("${spring.redis.pool.max-active}")
    private int maxTotal;

    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.pool.max-wait}")
    private int maxWaitMillis;

    @Value("${spring.redis.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.redis.testOnReturn}")
    private boolean testOnReturn;



    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        return jedisPoolConfig;
    }

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setDatabase(db);
        jedisConnectionFactory.setTimeout(timeout);
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig());
        return jedisConnectionFactory;
    }

    @Bean(name = "cityRedisTemplate")
    public RedisTemplate redisTemplate(){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();
        RedisSerializer<String> kryoRedisSerializer = new KryoRedisSerializer<String>();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(kryoRedisSerializer);
        return redisTemplate;
    }




}
