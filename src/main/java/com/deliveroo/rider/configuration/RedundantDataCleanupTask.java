package com.deliveroo.rider.configuration;

import com.deliveroo.rider.entity.City;
import com.deliveroo.rider.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RedundantDataCleanupTask {
    @Autowired
    private CityRepository cityRepository; // Assuming you have a CityRepository to interact with the database

    @Scheduled(cron = "0 */1 * * * *") // Run every 1 minutes
//    @Scheduled(cron = "0 0 0 * * *") // Scheduled to run daily at midnight
    public void removeRedundantData() {
        List<City> cities = cityRepository.findAll();
        List<City> uniqueCities = cities.stream()
                .collect(Collectors.toMap(
                        city -> city.getCityName().toLowerCase(), // Use lowercase city names as keys
                        city -> city,
                        (existing, replacement) -> existing // Merge strategy: Keep existing city if duplicates found
                ))
                .values()
                .stream()
                .collect(Collectors.toList());

        // Delete redundant data
        for (City city : cities) {
            if (!uniqueCities.contains(city)) {
                cityRepository.delete(city);
            }
        }
    }
}
