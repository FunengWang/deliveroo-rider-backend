package com.deliveroo.rider.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Entity(name = "delivery_order")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
}