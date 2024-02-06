package com.deliveroo.rider.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Activity {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_id")
    private List<Order> orders;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;
}
