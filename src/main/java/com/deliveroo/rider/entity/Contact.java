package com.deliveroo.rider.entity;

import com.deliveroo.rider.validation.PhoneNumber;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotBlank(message = "contact name is required!")
    private String name;

    @NotBlank(message = "contact phone is required!")
    @PhoneNumber(message = "Invalid phone number!")
    private String phone;
}
