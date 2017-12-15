package org.spring.springboot.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.dao.CityMapper;
import org.spring.springboot.model.City;
import org.spring.springboot.service.CityService;
import org.spring.springboot.utils.JedisTemplate;
import org.spring.springboot.utils.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author luckylau
 * @date 2017/12/13/013 10:11
 */
@Service
public class CityServiceImpl implements CityService{

    private static final Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);

    @Autowired
    private CityMapper cityMapper;

    @Resource(name ="cityRedisTemplate")
    private JedisTemplate jedisTemplate;

    @Override
    public Integer saveCity(City city) {
        if(city == null){
            return 0;
        }
        cityMapper.saveCity(city);
        RedisClient.set(jedisTemplate, generateCityCacheKey(city.getCityName()), city, 1L, TimeUnit.HOURS);
        return 1;
    }

    private String generateCityCacheKey(String cityName){
        return String.format("city_%s", cityName);
    }
}
