package com.deliveroo.rider.repository;

import com.deliveroo.rider.entity.City;
import com.deliveroo.rider.pojo.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CityRepository extends CrudRepository<City, Long> {
    List<City> findAll();

    List<City> findByCountry(Country country);
}
