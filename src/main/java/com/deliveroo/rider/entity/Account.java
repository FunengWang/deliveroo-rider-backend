package com.deliveroo.rider.entity;

import com.deliveroo.rider.serialization.deserializer.AccountTypeDeserializer;
import com.deliveroo.rider.pojo.*;
import com.deliveroo.rider.serialization.deserializer.LocalDateTimeDeserializer;
import com.deliveroo.rider.serialization.serializer.AccountTypeSerializer;
import com.deliveroo.rider.serialization.serializer.CallingCodeSerializer;
import com.deliveroo.rider.serialization.serializer.CountrySerializer;
import com.deliveroo.rider.serialization.serializer.WorkingTypeSerializer;
import com.deliveroo.rider.validation.FutureDatePattern;
import com.deliveroo.rider.validation.PhoneNumber;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
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
@JsonIgnoreProperties(ignoreUnknown = true) //兼容未知报文，不至于出错
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Long id;

    @Column(nullable = false, length = 15)
    @ApiModelProperty(required = true,example = "Joe")
    @NotBlank(message = "lastName must not be blank!")
    private String firstName;

    @Column(nullable = false, length = 15)
    @ApiModelProperty(required = true,example = "Biden")
    @NotBlank(message = "lastName must not be blank!")
    private String lastName;

    @Column(nullable = false, length = 30)
    @ApiModelProperty(required = true,example = "838761234")
    @NotNull(message = "phone is required!")
    @PhoneNumber(message = "Invalid phone number!")
    private String phone;

    @Column(length = 5)
    @JsonSerialize(using = CallingCodeSerializer.class)
    private CallingCode callingCode;

    @Column(nullable = false, length = 30, unique = true)
    @ApiModelProperty(required = true,example = "joe.biden@gmail.com")
    @NotNull(message = "email is required!")
    @Email(message = "Invalid email!")
    private String email;

    @Column(nullable = false, length = 10)
    @JsonDeserialize(using = AccountTypeDeserializer.class)
    @JsonSerialize(using = AccountTypeSerializer.class)
    @ApiModelProperty(notes = "default value is E-Bike",example = "e-bike")
    private AccountType accountType = AccountType.E_BIKE;

    @Column(nullable = false)
    @ApiModelProperty(required = true,example = "606029",notes = "6 digit string")
    @NotBlank(message = "securityCode is required!")
    private String securityCode;

    @Column(length = 15)
    @ApiModelProperty(hidden = true)
    @NotBlank(message = "areaName is required!")
    private String areaName;

    @Column(length = 10)
    @JsonSerialize(using = CountrySerializer.class)
    @NotNull(message = "country is required!")
    private Country country;

    @Column(length = 10)
    @JsonSerialize(using = WorkingTypeSerializer.class)
    @ApiModelProperty(notes = "default working type is normal",example = "normal")
    private WorkingType workingType = WorkingType.NORMAL;

    @Column(nullable = false, length = 10, unique = true)
    @ApiModelProperty(required = true,example = "823487",notes = "6 digit string")
    @NotBlank(message = "riderId is required!")
    @Size(min = 6,max = 6,message = "riderId is a 6 digit value!")
    private String riderId;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Activity> activities;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(example = "2024-05-01 17:30:00",notes = "if null or empty value then the account is non-expired forever")
    @FutureDatePattern
    //兼容 yyyy-mm-dd 和 yyyy-mm-dd HH:mm:ss 两种模式
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expirationDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_id")
    @NotNull(message = "contact is required!")
    @Valid
    private Contact contact;

    @JsonIgnore
    private boolean mocked;

    @JsonIgnore
    @NotNull(message = "newCreated is required!")
    private boolean newCreated;

    public CallingCode getCallingCode() {
        if(this.country == Country.UK){
            return CallingCode.UK;
        } else {
            return  CallingCode.IRELAND;
        }
    }

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

    @JsonIgnore
    public boolean notExpired(){
       return this.expirationDate !=null && this.expirationDate.isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", callingCode=" + callingCode +
                ", email='" + email + '\'' +
                ", accountType=" + accountType +
                ", securityCode='" + securityCode + '\'' +
                ", areaName='" + areaName + '\'' +
                ", country=" + country +
                ", workingType=" + workingType +
                ", riderId='" + riderId + '\'' +
                ", activities=" + activities +
                ", expirationDate=" + expirationDate +
                ", contact=" + contact +
                ", mocked=" + mocked +
                ", newCreated=" + newCreated +
                '}';
    }
}
