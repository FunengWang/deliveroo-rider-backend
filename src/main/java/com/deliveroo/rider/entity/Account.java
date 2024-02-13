package com.deliveroo.rider.entity;

import com.deliveroo.rider.serialization.deserializer.AccountTypeDeserializer;
import com.deliveroo.rider.pojo.*;
import com.deliveroo.rider.serialization.serializer.AccountTypeSerializer;
import com.deliveroo.rider.serialization.serializer.CallingCodeSerializer;
import com.deliveroo.rider.serialization.serializer.CountrySerializer;
import com.deliveroo.rider.serialization.serializer.WorkingTypeSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "rider_account",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"firstName","lastname"})}
)
@Data
@ToString
@ApiModel(value = "Rider Account Object",description = "Rider Account Object")
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Long id;

    @Column(nullable = false, length = 15)
    @ApiModelProperty(required = true,example = "Funeng")
    private String firstName;

    @Column(nullable = false, length = 15)
    @ApiModelProperty(required = true,example = "Wang")
    private String lastname;

    @Column(nullable = false, length = 30)
    @ApiModelProperty(required = true,example = "838761234")
    private String phone;

    @Column(length = 5)
    @JsonSerialize(using = CallingCodeSerializer.class)
    @ApiModelProperty(example = "+353",notes = "default is +353(Ireland calling code)")
    private CallingCode callingCode = CallingCode.IRELAND;

    @Column(nullable = false, length = 30, unique = true)
    @ApiModelProperty(required = true,example = "funengwang23@gmail.com")
    private String email;

    @Column(nullable = false, length = 10)
    @JsonDeserialize(using = AccountTypeDeserializer.class)
    @JsonSerialize(using = AccountTypeSerializer.class)
    @ApiModelProperty(notes = "default value is E-Bike",example = "e-bike")
    private AccountType accountType = AccountType.E_BIKE;

    @Column(nullable = false)
    @ApiModelProperty(required = true,example = "606029",notes = "6 digit string")
    private String securityCode;

    @Column(length = 15)
    @ApiModelProperty(hidden = true)
    private City city;

    @Column(length = 10)
    @JsonSerialize(using = CountrySerializer.class)
    @ApiModelProperty(notes = "default country is Ireland",example = "Ireland")
    private Country country = Country.IRELAND;

    @Column(length = 10)
    @JsonSerialize(using = WorkingTypeSerializer.class)
    @ApiModelProperty(notes = "default working type is normal",example = "normal")
    private WorkingType workingType = WorkingType.NORMAL;

    @Column(nullable = false, length = 10, unique = true)
    @ApiModelProperty(required = true,example = "823487",notes = "6 digit string")
    private String riderId;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Activity> activities;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(example = "2024-05-01 17:30:00",notes = "if null or empty value then the account is non-expired forever")
    private LocalDateTime expirationDate;

    public void compareAndFillFields(Account other) {
        Class<? extends Account> clazz = getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object thisVal = field.get(this);
                Object otherVal = field.get(other);
                if(otherVal != null && !thisVal.equals(otherVal)){
                    field.set(this,otherVal);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
