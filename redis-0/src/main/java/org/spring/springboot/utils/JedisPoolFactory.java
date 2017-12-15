package org.spring.springboot.utils;

import org.springframework.beans.factory.InitializingBean;
import org.spring.springboot.utils.RedisConnectionConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @author luckylau
 * @date 2017/12/15/015 14:12
 */
public class JedisPoolFactory implements InitializingBean {
    private String host = Protocol.DEFAULT_HOST;
    private int port = Protocol.DEFAULT_PORT;
    private int timeout = Protocol.DEFAULT_TIMEOUT;
    private String password = null;
    // Default Redis DB
    private int db = Protocol.DEFAULT_DATABASE;

    private RedisConnectionConfig connectionConfig;

    private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

    public JedisPoolFactory(JedisPoolConfig jedisPoolConfig){
        this.jedisPoolConfig = jedisPoolConfig;
    }

    public Pool<Jedis> createPool(){
        return new JedisPool(jedisPoolConfig, connectionConfig.getHost(),connectionConfig.getPort(),connectionConfig.getTimeout(),
                connectionConfig.getPassword(),connectionConfig.getDb());
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if( null == connectionConfig){
            connectionConfig = new RedisConnectionConfig(host, port, timeout, password ,db);
        }


    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }
}
