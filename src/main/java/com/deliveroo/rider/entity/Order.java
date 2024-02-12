package com.deliveroo.rider.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Entity(name = "delivery_order")
@Data
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false,precision = 2)
    private Double fee;

    @Column(precision = 2,nullable = true)
    private Double extra;

    @Column(precision = 2,nullable = true)
    private Double tip;

    @Column(nullable = false, length = 10)
    private String place;

    @Column(nullable = false,length = 20)
    private String shop;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderDetail> orderDetails;
}