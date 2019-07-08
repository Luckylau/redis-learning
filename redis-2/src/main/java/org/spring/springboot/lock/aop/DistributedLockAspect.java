package org.spring.springboot.lock.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.spring.springboot.lock.DistributedLock;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @Author luckylau
 * @Date 2019/7/8
 */
@Aspect
@Component
@Slf4j
public class DistributedLockAspect {

    @Resource(name = "distributedLock")
    private DistributedLock distributedLock;

    @Pointcut(value = "@annotation(org.spring.springboot.lock.aop.RedisLock)")
    public void annotationPointCut() {
    }

    @Around("annotationPointCut()")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);

        String lockIdentifier = distributedLock.lockIdentifier();
        log.debug("current thread :{} lockIdentifier: {}", Thread.currentThread().getName(), lockIdentifier);
        String lockKey = redisLock.key();
        boolean tryLock = redisLock.tryLock();

        try {
            if (tryLock) {
                int timeout = redisLock.timeout();
                TimeUnit timeUnit = redisLock.timeUnit();
                if (distributedLock.tryLock(lockKey, lockIdentifier, timeout, timeUnit)) {
                    return joinPoint.proceed();
                }
                log.warn("thread {} try Lock error.", Thread.currentThread().getName());
            } else {
                if (distributedLock.lock(lockKey, lockIdentifier)) {
                    return joinPoint.proceed();
                }
            }
        } catch (InterruptedException e) {
            log.error("thread {} throw InterruptedException.", Thread.currentThread().getName(), e);
        } catch (Throwable throwable) {
            log.error(" DistributedLock throwable: {}", throwable);
        } finally {
            distributedLock.unlock(lockKey, lockIdentifier);
        }
        return null;
    }

}
