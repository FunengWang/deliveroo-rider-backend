package com.deliveroo.rider.repository;

import com.deliveroo.rider.entity.OrderDetail;
import org.springframework.data.repository.CrudRepository;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, Long> {
}
