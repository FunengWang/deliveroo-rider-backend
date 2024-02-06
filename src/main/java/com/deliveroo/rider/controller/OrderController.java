package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.Order;
import com.deliveroo.rider.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class OrderController {
    @Autowired
    private OrderRepository repository;
    @GetMapping("/order/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") Long id){
        Optional<Order> optional = repository.findById(id);
        return optional.map(order -> ResponseEntity.ok().body(order)).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
