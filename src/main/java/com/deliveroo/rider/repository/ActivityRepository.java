package com.deliveroo.rider.repository;

import com.deliveroo.rider.entity.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends CrudRepository<Activity, Long> {
    @Query(value = "SELECT * FROM activity  WHERE YEAR(date) = :year and MONTH(date) = :month and account_id = :accountId",nativeQuery = true)
    List<Activity> findByDateInYearAndMonth(@Param("year") int year, @Param("month") int month, @Param("accountId") long accountId);

    @Query(value = "SELECT a.* FROM activity a inner join delivery_order o on o.activity_id = a.id WHERE a.date BETWEEN :from AND :to and a.account_id = :accountId", nativeQuery = true)
    List<Activity> findDataInDateRange(@Param("from") Date from,@Param("to") Date to, @Param("accountId") long accountId);
}
