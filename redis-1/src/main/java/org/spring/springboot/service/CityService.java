package org.spring.springboot.service;

import org.spring.springboot.model.City;

import java.util.List;

/**
 * @author luckylau
 * @date 2017/12/13/013 10:10
 */
public interface CityService {

    Integer saveCity(City city);

    Integer updateCity(City city);

    City getCity(String cityName);

    Integer deleteCity(String cityName);

    List<City> getAllCity(List<String> city);
}
