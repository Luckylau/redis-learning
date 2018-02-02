package org.spring.springboot.service.Impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.dao.CityMapper;
import org.spring.springboot.domain.CityEntity;
import org.spring.springboot.model.City;
import org.spring.springboot.service.CityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.spring.springboot.domain.CityEntity.generateCityCacheKey;

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
    private RedisTemplate redisTemplate;

    @Override
    public Integer saveCity(City city) {
        if(city == null){
            return 0;
        }
        CityEntity cityEntity = new CityEntity();
        BeanUtils.copyProperties(city, cityEntity);
        cityMapper.saveCity(cityEntity);
        return 1;
    }

    @Override
    public Integer updateCity(City city) {
        CityEntity cityEntity = new CityEntity();
        BeanUtils.copyProperties(city, cityEntity);
        cityMapper.updateCity(cityEntity);
        return 1;
    }

    @Override
    public City getCity(String cityName) {
        cityMapper.getCity(cityName);
        return null;
    }

    @Override
    public Integer deleteCity(String cityName) {
        cityMapper.deleteCity(cityName);
        return 1;
    }

    @Override
    public List<City> getAllCity(List<String> citys) {

        return null;
    }

}
