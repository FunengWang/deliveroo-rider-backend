package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.Area;
import com.deliveroo.rider.pojo.Country;
import com.deliveroo.rider.pojo.dto.AreaInfo;
import com.deliveroo.rider.pojo.dto.CommonResult;
import com.deliveroo.rider.repository.AreaRepository;
import com.deliveroo.rider.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class AreaController {
    @Autowired
    private AreaRepository areaRepository;

    @GetMapping("/areas/country/{country}")
    public CommonResult<List<Area>> findByCountry(@PathVariable Country country){
        List<Area> areas = areaRepository.findByCountry(country);
        return new CommonResult<List<Area>>().generateOK(null,areas);
    }

    @GetMapping("/areas/{areaId}")
    public CommonResult<Map<String, List<AreaInfo>>> findRelatedCities(@PathVariable("areaId") Long areaId){
        Optional<Area> optional = areaRepository.findById(areaId);
        if(optional.isPresent()) {
            Area currentArea = optional.get();
            List<Area> allAreas = areaRepository.findByCountry(currentArea.getCountry());
            List<AreaInfo> areas = allAreas.stream().map(ele -> {
                AreaInfo cityInfo = new AreaInfo();
                cityInfo.setAreaName(ele.getAreaName());
                cityInfo.setLatitude(ele.getLatitude());
                cityInfo.setLongitude(ele.getLongitude());
                cityInfo.setAbbreviation(ele.getAbbreviation());
                cityInfo.setCountry(ele.getCountry());
                cityInfo.setId(ele.getId());
                cityInfo.setCurrent(currentArea);
                cityInfo.setBusy(Utils.getRandomBusy());
                return cityInfo;
            }).collect(Collectors.toList());
            areas.sort(Comparator.comparing(ele->ele.getAreaName()));
            Map<String, List<AreaInfo>> collect = areas
                    .stream()
                    .collect(Collectors.groupingBy(ele -> ele.getAreaName().substring(0, 1)));
            return new CommonResult<Map<String, List<AreaInfo>>>().generateOK(null, collect);
        } else {
            return new CommonResult<Map<String, List<AreaInfo>>>().generateBadRequest("Can't find this area!",null);
        }
    }
}
