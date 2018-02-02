package org.spring.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * @author luckylau
 * @date 2018/2/1/001 19:30
 */

@SpringBootApplication
@MapperScan("org.spring.springboot.dao")
public class Application {
    public static void main(String[] args) {
        // 程序启动入口
        SpringApplication.run(Application.class,args);
    }
}
