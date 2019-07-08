package org.spring.springboot.lock.aop;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义注解，结合AOP实现DistributedLock
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RedisLock {

    /**
     * @return
     */
    String key();

    /**
     * @return
     */
    boolean tryLock() default false;

    /**
     * @return
     */
    int timeout() default -1;

    /**
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;


}