package com.deliveroo.rider.repository;

import com.deliveroo.rider.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order,Long> {
}
