package org.spring.springboot.service;

import org.spring.springboot.lock.aop.RedisLock;
import org.springframework.stereotype.Service;

/**
 * @Author luckylau
 * @Date 2019/7/8
 */
@Service
public class CounterService {
    private int count ;

    @RedisLock(key ="counter")
    public void incrementAndGet(){
        count ++;
    }

    @RedisLock(key ="counter")
    public void decrementAndGet(){
        count --;
    }

    public int getCount(){
        return count;
    }
}
