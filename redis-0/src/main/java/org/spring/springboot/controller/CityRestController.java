package org.spring.springboot.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.model.City;
import org.spring.springboot.service.CityService;
import org.spring.springboot.service.Impl.CityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luckylau
 * @date 2017/12/13/013 10:12
 */
@RestController
@Api(value = "API统一管理入口")
public class CityRestController {
    private static final Logger logger = LoggerFactory.getLogger(CityRestController.class);

    @Autowired
    private CityService cityService;

    @ApiImplicitParam(name = "city", value = "city entity",required = true ,dataType = "City")
    @RequestMapping(value = "/city/create", method = RequestMethod.POST)
    public void createCity(@RequestBody City city) {
        logger.info("create city ...");
        cityService.saveCity(city);
    }
}
