package com.deliveroo.rider.repository;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.entity.Activity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActivityRepository extends CrudRepository<Activity, Long> {
    @EntityGraph(attributePaths = {"orders"})
    List<Activity> findByAccount(Account account);
}
