package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.Order;
import com.deliveroo.rider.pojo.dto.CommonResult;
import com.deliveroo.rider.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class OrderController {
    @Autowired
    private OrderRepository repository;
    @GetMapping("/order/{id}")
    public CommonResult<Order> getOrder(@PathVariable("id") Long id){
        Optional<Order> optional = repository.findById(id);
        if(optional.isPresent()){
            return new CommonResult<Order>().generateOK(null,optional.get());
        }else {
            return new CommonResult().generateBadRequest(null,null);
        }
    }
}
