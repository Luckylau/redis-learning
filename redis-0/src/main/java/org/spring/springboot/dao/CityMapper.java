package org.spring.springboot.dao;

import org.spring.springboot.domain.CityEntity;

import java.util.List;

/**
 * @author luckylau
 * @date 2017/12/13/013 9:52
 */
public interface CityMapper {
    Integer saveCity(CityEntity cityEntity);

    Integer updateCity(CityEntity cityEntity);

    CityEntity getCity(String cityName);

    Integer deleteCity(String cityName);

    List<CityEntity> getAllCity(List<String> cityNames);
}
