package com.deliveroo.rider.pojo.dto;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.entity.Area;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountInfo extends Account {
    private Area area;
}
