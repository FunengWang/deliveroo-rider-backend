package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.FeeBoost;
import com.deliveroo.rider.pojo.DayOfWeek;
import com.deliveroo.rider.pojo.dto.CommonResult;
import com.deliveroo.rider.pojo.dto.FeeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.deliveroo.rider.util.Utils.convertToFeeBoosts;
import static com.deliveroo.rider.util.Constants.*;

@RestController
public class FeeController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/feeBoost/{day}")
    public CommonResult<List<FeeInfo>> getFeeBoostsByDay(@PathVariable("day") DayOfWeek dayOfWeek) {
        List<FeeBoost> feeBoostList = new ArrayList<>();
        BoundSetOperations<String, Object> set = redisTemplate.boundSetOps(FEE_BOOSTS_KEY);
        List<FeeBoost> allFeeBoosts = convertToFeeBoosts(set.members());
        switch (dayOfWeek) {
            case MONDAY:
                feeBoostList = allFeeBoosts
                        .stream()
                        .filter(ele -> ele.getDayOfWeek().ordinal() == DayOfWeek.MONDAY.ordinal())
                        .collect(Collectors.toList());
                break;
            case TUESDAY:
                feeBoostList = allFeeBoosts
                        .stream()
                        .filter(ele -> ele.getDayOfWeek().ordinal() == DayOfWeek.TUESDAY.ordinal())
                        .collect(Collectors.toList());
                break;
            case WEDNESDAY:
                feeBoostList = allFeeBoosts
                        .stream()
                        .filter(ele -> ele.getDayOfWeek().ordinal() == DayOfWeek.WEDNESDAY.ordinal())
                        .collect(Collectors.toList());
                break;
            case THURSDAY:
                feeBoostList = allFeeBoosts
                        .stream()
                        .filter(ele -> ele.getDayOfWeek().ordinal() == DayOfWeek.THURSDAY.ordinal())
                        .collect(Collectors.toList());
                break;
            case FRIDAY:
                feeBoostList = allFeeBoosts
                        .stream()
                        .filter(ele -> ele.getDayOfWeek().ordinal() == DayOfWeek.FRIDAY.ordinal() ||
                                ele.getDayOfWeek().ordinal() == DayOfWeek.SATURDAY.ordinal() ||
                                ele.getDayOfWeek().ordinal() == DayOfWeek.SUNDAY.ordinal())
                        .collect(Collectors.toList());
                break;
            case SATURDAY:
                feeBoostList = allFeeBoosts
                        .stream()
                        .filter(ele -> ele.getDayOfWeek().ordinal() == DayOfWeek.SATURDAY.ordinal() || ele.getDayOfWeek().ordinal() == DayOfWeek.SUNDAY.ordinal())
                        .collect(Collectors.toList());
                break;
            case SUNDAY:
                feeBoostList = allFeeBoosts
                        .stream()
                        .filter(ele -> ele.getDayOfWeek().ordinal() == DayOfWeek.SUNDAY.ordinal())
                        .collect(Collectors.toList());
                break;
        }
        if (!feeBoostList.isEmpty()) {
            return new CommonResult<List<FeeInfo>>().generateOK(null,formatFeeBoostList(feeBoostList));
        } else {
            return new CommonResult().generateBadRequest(null,null);
        }
    }


    private List<FeeInfo> formatFeeBoostList(List<FeeBoost> feeBoostList) {
        Map<String, List<FeeBoost>> map = feeBoostList.stream()
                .collect(Collectors.groupingBy(ele -> ele.getDayOfWeek().toString()));
        List<FeeInfo> feeInfoList = new ArrayList<>();
        for (Map.Entry<String, List<FeeBoost>> entry : map.entrySet()) {
            FeeInfo feeInfo = new FeeInfo();
            feeInfo.setDate(entry.getKey());
            feeInfo.setFeeList(entry.getValue());
            feeInfoList.add(feeInfo);
        }
        feeInfoList.sort(Comparator.comparing(a -> DayOfWeek.valueOf(a.getDate())));
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH);
        for (int i = 0; i < feeInfoList.size(); i++) {
            FeeInfo feeInfo = feeInfoList.get(i);
            LocalDate nextDay = currentDate.plusDays(i);
            feeInfo.setDate(String.format("%s %s", feeInfo.getDate(), nextDay.format(formatter).toUpperCase()));
        }
        return feeInfoList;
    }
}
