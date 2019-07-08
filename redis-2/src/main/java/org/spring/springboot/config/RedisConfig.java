package org.spring.springboot.config;

import org.spring.springboot.lock.DistributedLock;
import org.spring.springboot.lock.RedisDistributedLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author luckylau
 * @Date 2019/7/8
 */
@Configuration
public class RedisConfig {
    @Value("${redis.cache.clusterNodes}")
    private String clusterNodes;
    @Value("${redis.cache.password}")
    private String password;
    @Value("${redis.cache.maxTotal}")
    private int maxTotal;
    @Value("${redis.cache.maxIdle}")
    private int maxIdle;
    @Value("${redis.cache.maxWaitMillis}")
    private int maxWaitMillis;
    @Value("${redis.cache.connTimeout}")
    private int connTimeout;
    @Value("${redis.cache.soTimeout}")
    private int soTimeout;
    @Value("${redis.cache.maxAttempts}")
    private int maxAttempts;

    /**
     * 单位：ms
     */
    private final static int LOCK_TIME_OUT = 8000;

    @Bean
    @Primary
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        return jedisPoolConfig;
    }

    @Bean
    @Primary
    public JedisCluster getJedisCluster(JedisPoolConfig jedisPoolConfig) {
        String[] servers = clusterNodes.split(",");
        Set<HostAndPort> hosts = new HashSet<>();
        for (String server : servers) {
            String[] ipPortEntry = server.split(":");
            hosts.add(new HostAndPort(ipPortEntry[0], Integer.valueOf(ipPortEntry[1])));
        }
        return new JedisCluster(hosts, connTimeout, soTimeout, maxAttempts, password, jedisPoolConfig);
    }

    @Bean(name = "distributedLock")
    @Primary
    public DistributedLock getDistributedLock(JedisCluster jedisCluster) {
        RedisDistributedLock redisDistributedLock = new RedisDistributedLock(jedisCluster, LOCK_TIME_OUT);
        return redisDistributedLock;
    }
}
