package org.spring.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author luckylau
 * @date 2018/2/1/001 19:30
 */

@SpringBootApplication
@MapperScan("org.spring.springboot.dao")
public class Redis1 {
    public static void main(String[] args) {
        // 程序启动入口
        SpringApplication.run(Redis1.class, args);
    }
}
