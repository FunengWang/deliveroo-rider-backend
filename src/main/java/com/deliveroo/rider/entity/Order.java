package com.deliveroo.rider.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import static com.deliveroo.rider.util.Utils.formatDouble;


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
    @JsonIgnore
    private Activity activity;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @JsonIgnore
    public boolean hasSubOrder() {
        return this.getOrderDetails().size() > 1;
    }

    @JsonIgnore
    public double getEarnings(){
        double earnings = BigDecimal.valueOf(this.getFee())
                .add(BigDecimal.valueOf(this.getExtra()))
                .add(BigDecimal.valueOf(this.getTip()))
                .doubleValue();
        return formatDouble(earnings);
    }
    @JsonIgnore
    public LocalTime getStartTime(){
        return this.getOrderDetails()
                .stream()
                .min(Comparator.comparing(OrderDetail::getStart))
                .get()
                .getStart();

    }
    @JsonIgnore
    public LocalTime getCompleteTime(){
        return this.getOrderDetails()
                .stream()
                .max(Comparator.comparing(OrderDetail::getComplete))
                .get()
                .getComplete();
    }
}