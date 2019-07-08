package org.spring.springboot.lock;

import com.sun.istack.internal.NotNull;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {

    /**
     * 在一定时间内获取获取
     *
     * @param lockIdentifier
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    boolean tryLock(@NotNull String lockIdentifier, long time, @NotNull TimeUnit unit) throws InterruptedException;


    /**
     * @param lockKey
     * @param lockIdentifier
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    boolean tryLock(@NotNull String lockKey, @NotNull String lockIdentifier, long time, @NotNull TimeUnit unit) throws InterruptedException;


    /**
     * @param lockKey
     * @param lockIdentifier
     * @return
     */
    boolean lock(@NotNull String lockKey, @NotNull String lockIdentifier);
    /**
     * 获取锁
     *
     * @param lockIdentifier
     * @return
     */
    boolean lock(@NotNull String lockIdentifier);

    /**
     * 解锁
     *
     * @param lockIdentifier
     */
    void unlock(@NotNull String lockIdentifier);

    /**
     * 解锁
     * @param lockKey
     * @param lockIdentifier
     */
    void unlock(@NotNull String lockKey, @NotNull String lockIdentifier);

    /**
     * 获取标识
     *
     * @return
     */
    String lockIdentifier();

}
