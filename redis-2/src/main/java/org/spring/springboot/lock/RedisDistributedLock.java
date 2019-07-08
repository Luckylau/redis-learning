package org.spring.springboot.lock;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author luckylau
 * @Date 2019/7/8
 */
@Slf4j
public class RedisDistributedLock implements DistributedLock {

    private final JedisCluster jedisCluster;

    /**
     * 单位是毫秒
     */
    private final int lockTimeOut;

    /**
     * 默认时间，单位是毫秒
     */
    private final int retryAwait = 10;
    /**
     * 锁的名字
     */
    private String lockKey;


    public RedisDistributedLock(JedisCluster jedisCluster, String lockKey, int lockTimeOut) {
        this.jedisCluster = jedisCluster;
        this.lockKey = lockKey;
        this.lockTimeOut = lockTimeOut;
    }

    public RedisDistributedLock(JedisCluster jedisCluster, int lockTimeOut) {
        this.jedisCluster = jedisCluster;
        this.lockTimeOut = lockTimeOut;
    }

    @Override
    public boolean tryLock(String lockIdentifier, long time, TimeUnit unit) throws InterruptedException {
        return tryLock(lockKey, lockIdentifier, time, unit);
    }

    @Override
    public boolean tryLock(String lockKey, String lockIdentifier, long time, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Boolean result = tryRedisLock(lockKey, lockIdentifier, time, unit);
        if (result) {
            log.debug("thread: {} occupy lock success 。", Thread.currentThread().getName());
            return true;
        }
        log.debug("thread: {} occupy lock failure 。", Thread.currentThread().getName());
        return false;
    }

    @Override
    public boolean lock(String lockKey, String lockIdentifier) {
        int count = 0;
        long start = System.currentTimeMillis();
        for (; ; ) {
            Boolean result = createRedisKey(lockKey, lockIdentifier);
            count++;
            if (result) {
                long end = System.currentTimeMillis();
                log.debug("thread: {} occupy lock success， count: {}, time : {} ms.", Thread.currentThread().getName(), count, end - start);
                return true;
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(retryAwait));
        }
    }

    @Override
    public boolean lock(String lockIdentifier) {
        return lock(lockKey, lockIdentifier);
    }

    @Override
    public void unlock(String lockIdentifier) {
        unlock(lockKey, lockIdentifier);
    }

    @Override
    public void unlock(String lockKey, String lockIdentifier) {
        try {
            boolean res = deleteRedisKey(lockKey, lockIdentifier);
            if (res) {
                log.debug("thread: {} release lock success .", Thread.currentThread().getName());
            }
        } catch (Exception e) {
            log.error("unlock error", e);
        }
    }


    @Override
    public String lockIdentifier() {
        return UUID.randomUUID().toString();
    }

    private Boolean tryRedisLock(String lockKey, String lockIdentifier, long time, TimeUnit timeUnit) {
        final long startMillis = System.currentTimeMillis();
        final long millisToWait = time < 0 ? Long.MAX_VALUE : timeUnit.toMillis(time);
        for (; ; ) {
            Boolean result = createRedisKey(lockKey, lockIdentifier);
            if (result) {
                return true;
            }
            if (System.currentTimeMillis() - startMillis > millisToWait) {
                break;
            }

            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(retryAwait));
        }
        return false;
    }

    private Boolean createRedisKey(String lockKey, String lockIdentifier) {
        try {
            String luaScript = ""
                    + "\nlocal r = tonumber(redis.call('SETNX', KEYS[1],ARGV[1]));"
                    + "\nif r == 1 then"
                    + "\nredis.call('PEXPIRE',KEYS[1],ARGV[2]);"
                    + "\nend"
                    + "\nreturn r";
            List<String> keys = new ArrayList<>();
            keys.add(lockKey);
            List<String> argv = new ArrayList<>();
            argv.add(lockIdentifier);
            argv.add(String.valueOf(lockTimeOut));
            long res = (Long) jedisCluster.eval(luaScript, keys, argv);
            if (res == 1) {
                return true;
            }

        } catch (Exception e) {
            log.error("redis distributed reentrantLock create key error.", e);
        }
        return false;
    }

    private boolean deleteRedisKey(String lockKey, String lockIdentifier) {
        String luaScript = ""
                + "\nlocal v = redis.call('GET', KEYS[1]);"
                + "\nlocal r= 0;"
                + "\nif v == ARGV[1] then"
                + "\nr =redis.call('DEL',KEYS[1]);"
                + "\nend"
                + "\nreturn r";
        List<String> keys = new ArrayList<String>();
        keys.add(lockKey);
        List<String> argv = new ArrayList<String>();
        argv.add(lockIdentifier);
        long r = (Long) jedisCluster.eval(luaScript, keys, argv);
        if (r == 1) {
            return true;
        }
        return false;
    }
}