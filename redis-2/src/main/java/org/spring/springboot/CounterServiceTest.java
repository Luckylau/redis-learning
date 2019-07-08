package org.spring.springboot;

import lombok.extern.slf4j.Slf4j;
import org.spring.springboot.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author luckylau
 * @Date 2019/7/8
 */
@Service
@Slf4j
public class CounterServiceTest {
    @Autowired
    private CounterService counterService;

    @PostConstruct
    public void init(){
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for(int i = 0 ; i < 100 ;i++) {
            if(i < 50) {
                executorService.execute(() -> {
                    counterService.incrementAndGet();
                });
            }else {
                executorService.execute(() -> {
                    counterService.decrementAndGet();
                });
            }
        }
        log.info("count: {}", counterService.getCount());
    }

}

