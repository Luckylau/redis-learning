# Redis-Learning
### Redis使用教程

**redis-0** 项目主要通过**单纯jedis的方式**来实现对redis的操作，封装在[RedisClient](https://github.com/Luckylau/Redis-Learning/blob/master/redis-0/src/main/java/org/spring/springboot/utils/RedisClient.java)中。

这个版本的特点是：

1.用配置xml文件的方式加载bean, bean定义在[applicationContext.xml](https://github.com/Luckylau/Redis-Learning/blob/master/redis-0/src/main/resources/applicationContext.xml)。

2.mybatis访问数据库，使用了alibaba的druid源。

3.集成了swagger2的使用。

4.log4j的配置使用。

5.用Threadlocal方式实现[KryoRedisSerializer](https://github.com/Luckylau/Redis-Learning/blob/master/redis-0/src/main/java/org/spring/springboot/utils/serialize/KryoRedisSerializer.java)的并发使用。