package com.deliveroo.rider.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    @OneToMany(mappedBy = "activity",cascade = CascadeType.ALL)
    private List<Order> orders;

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", date=" + date +
                ", orders=" + orders +
                '}';
    }
}
