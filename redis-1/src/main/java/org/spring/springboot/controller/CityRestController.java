package org.spring.springboot.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.model.City;
import org.spring.springboot.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author luckylau
 * @date 2017/12/13/013 10:12
 */
@RestController
@Api(value = "API统一管理入口")
@RequestMapping("/city")
public class CityRestController {
    private static final Logger logger = LoggerFactory.getLogger(CityRestController.class);

    @Autowired
    private CityService cityService;

    @ApiImplicitParam(name = "city", value = "city entity",required = true ,dataType = "City")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void createCity(@RequestBody City city) {
        logger.info("create city ...");
        cityService.saveCity(city);
    }

    @ApiImplicitParam(name = "city", value = "city name",paramType = "query", required = true ,dataType = "String")
    @RequestMapping(value = "get", method = RequestMethod.GET)
    public City findOneCity(@RequestParam(value = "city") String city) {
        logger.info("find one city ...");
        return cityService.getCity(city);
    }

    @ApiImplicitParam(name = "city", value = "city entity",required = true ,dataType = "City")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public void modifyCity(@RequestBody City city) {
        logger.info("update one city ...");
        cityService.updateCity(city);
    }

    @ApiImplicitParam(name = "city", value = "city name", paramType = "query", required = true ,dataType = "String")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void modifyCity(@RequestParam(value = "city") String city) {
        logger.info("delete one city ...");
        cityService.deleteCity(city);
    }

    @RequestMapping(value = "getAll", method = RequestMethod.GET)
    public List<City> findAllCity(@RequestParam(value = "city") String[] city) {
        logger.info("find all city ...");
        return cityService.getAllCity(Arrays.asList(city));
    }

}
