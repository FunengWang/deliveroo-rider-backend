package com.deliveroo.rider.scheduled;

import com.deliveroo.rider.entity.Area;
import com.deliveroo.rider.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RedundantDataCleanupTask {
    @Autowired
    private AreaRepository areaRepository;

//    @Scheduled(cron = "0 */1 * * * *") // Run every 1 minutes
    @Scheduled(cron = "0 0 0 * * *") // Scheduled to run daily at midnight
    public void removeRedundantData() {
        List<Area> areas = areaRepository.findAll();
        List<Area> uniqueCities = areas.stream()
                .collect(Collectors.toMap(
                        area -> area.getAreaName().toLowerCase(), // Use lowercase city names as keys
                        area -> area,
                        (existing, replacement) -> existing // Merge strategy: Keep existing city if duplicates found
                ))
                .values()
                .stream()
                .collect(Collectors.toList());

        // Delete redundant data
        for (Area area : areas) {
            if (!uniqueCities.contains(area)) {
                areaRepository.delete(area);
            }
        }
    }
}
