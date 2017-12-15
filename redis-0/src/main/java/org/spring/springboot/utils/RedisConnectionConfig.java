package org.spring.springboot.utils;

/**
 * @author luckylau
 * @date 2017/12/15/015 14:13
 */
public class RedisConnectionConfig {
    private String host;
    private int port;
    private int timeout;
    private String password;
    // Default Redis DB
    private int db;

    public RedisConnectionConfig(String host, int port, int timeout, String password, int db) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.password = password;
        this.db = db;
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
