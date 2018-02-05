package org.spring.springboot.service.Impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.dao.CityMapper;
import org.spring.springboot.domain.CityEntity;
import org.spring.springboot.model.City;
import org.spring.springboot.service.CityService;
import org.spring.springboot.utils.RedisClient;
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
import java.util.stream.Collectors;

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
        deleteCache(city.getCity());
        return 1;
    }

    @Override
    public City getCity(String cityName) {
        CityEntity cityEntity =RedisClient.get(redisTemplate, generateCityCacheKey(cityName), new RedisClient.Loader<String, CityEntity>() {
            @Override
            public CityEntity load(String key) throws CacheException {
                return cityMapper.getCity(cityName);
            }
        }, 1L, TimeUnit.HOURS);
        if(cityEntity == null){
            return null;
        }
        City city = new City();
        BeanUtils.copyProperties(cityEntity, city);
        return city;
    }

    @Override
    public Integer deleteCity(String cityName) {
        cityMapper.deleteCity(cityName);
        deleteCache(cityName);
        return 1;
    }

    @Override
    public List<City> getAllCity(List<String> citys) {
        if(citys == null || citys.isEmpty()){
            return null;
        }
        List<City> cityList = new ArrayList<>();
        //去重复
        citys = new ArrayList<>(new HashSet<>(citys));

        List<String> keys = new ArrayList<>();
        for(String city : citys){
            String key = generateCityCacheKey(city);
            keys.add(key);
        }
        //先从redis取数据
        List<CityEntity> cityEntities= RedisClient.mget(redisTemplate, keys);

        if(cityEntities == null){
            cityEntities = new ArrayList<>();
        }

        for(CityEntity cityEntity : cityEntities){
            if(cityEntity != null) {
                City city = new City();
                BeanUtils.copyProperties(cityEntity, city);
                cityList.add(city);
                citys.remove(city.getCity());
            }
        }

        if(citys.isEmpty()){
            return cityList;
        }

        List<CityEntity> remainCityEntities = cityMapper.getAllCity(citys);
        Map<String, CityEntity> map = remainCityEntities.stream().collect(Collectors.toMap(CityEntity::getCity, c -> c, (k1, k2) -> k1));

        RedisClient.mset(redisTemplate, map, 1L, TimeUnit.HOURS);
        for(CityEntity remaincityEntity: remainCityEntities){
            if(remaincityEntity != null){
                City city = new City();
                BeanUtils.copyProperties(remaincityEntity, city);
                cityList.add(city);
            }
        }

        return cityList;
    }

    private void deleteCache(String city){
        //处理中文
        String md5City = DigestUtils.md5Hex(city);
        RedisClient.delete(redisTemplate, md5City);
    };

}
