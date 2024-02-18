package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.City;
import com.deliveroo.rider.pojo.Country;
import com.deliveroo.rider.pojo.dto.CommonResult;
import com.deliveroo.rider.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CityController {
    @Autowired
    private CityRepository cityRepository;

    @GetMapping("/cities/{country}")
    public CommonResult<List<City>> findByCountry(@PathVariable Country country){
        List<City> cities = cityRepository.findByCountry(country);
        return new CommonResult<List<City>>().generateOK(null,cities);
    }
}
