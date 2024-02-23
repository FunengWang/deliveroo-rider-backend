package com.deliveroo.rider.repository;

import com.deliveroo.rider.entity.Area;
import com.deliveroo.rider.pojo.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AreaRepository extends CrudRepository<Area, Long> {
    List<Area> findAll();
    List<Area> findByCountry(Country country);

    List<Area> findByAreaNameContainingIgnoreCase(String areaName);
}
