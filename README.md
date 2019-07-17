# Redis-Learning
### Redis使用教程

Redis的入门，见[这篇文章](http://luckylau.tech/2017/08/13/SpringBoot%E4%B9%8BRedis/)。

**redis-0** 项目主要通过**单纯jedis的方式**来实现对redis的操作，封装在[RedisClient](https://github.com/Luckylau/Redis-Learning/blob/master/redis-0/src/main/java/org/spring/springboot/utils/RedisClient.java)中。

这个版本的特点是：

1.用配置xml文件的方式加载bean, bean定义在[applicationContext.xml](https://github.com/Luckylau/Redis-Learning/blob/master/redis-0/src/main/resources/applicationContext.xml)；

2.mybatis访问数据库，使用了alibaba的druid源；

3.集成了swagger2的使用；

4.log4j.xml配置log4j使用；

5.用**Threadlocal**方式实现[KryoRedisSerializer](https://github.com/Luckylau/Redis-Learning/blob/master/redis-0/src/main/java/org/spring/springboot/utils/serialize/KryoRedisSerializer.java)的并发使用；

**redis-1**项目主要通过Springboot的RedisTemplate来实现对redis的操作，封装在[RedisClient](https://github.com/Luckylau/Redis-Learning/blob/master/redis-1/src/main/java/org/spring/springboot/utils/RedisClient.java)中。

这个版本的特点是：

1.redis的bean通过@Bean方式加载，在[RedisConfiguration](https://github.com/Luckylau/Redis-Learning/blob/master/redis-1/src/main/java/org/spring/springboot/utils/RedisConfiguration.java。)中；

2.mybatis访问数据库，**springboot零配置方式**，使用了alibaba的druid源，同时可以**访问监控页面**http://xxxx:8080/druid/login.html ；

3.集成了swagger2的使用；

4.log4j.properties配置log4j使用；

5.使用**KryoPool**的方式实现[KryoRedisSerializer](https://github.com/Luckylau/Redis-Learning/blob/master/redis-1/src/main/java/org/spring/springboot/utils/serialize/KryoRedisSerializer.java)的并发使用；

**redis-2** 项目是一个基于Redis实现的分布式锁

特点是：

1.支持注解**RedisLock**加锁；

2.基于**redis集群**环境，用**jediscluster**执行lur脚本；

3.logback.xml配置**logback**日志；