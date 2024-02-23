package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.entity.Activity;
import com.deliveroo.rider.entity.Area;
import com.deliveroo.rider.pojo.dto.AccountInfo;
import com.deliveroo.rider.pojo.dto.CommonResult;
import com.deliveroo.rider.repository.AccountRepository;
import com.deliveroo.rider.repository.AreaRepository;
import com.deliveroo.rider.service.ActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@Api(tags = "Account Administration")
@Validated
public class AccountController {
    @Autowired
    private AccountRepository repository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ActivityService activityService;

    @PutMapping("/account")
    @ApiOperation(value = "Create new account", notes = "Create new account and add mocked activities this account")
    public CommonResult createAccount(@RequestBody @Valid Account account) {
        Optional<Account> optional = repository.findByRiderId(account.getRiderId());
        if (optional.isPresent()) {
            return new CommonResult<>().generateBadRequest("Account already existed. Can't create duplicate account!",null);
        } else {
            account.setSecurityCode(passwordEncoder.encode(account.getSecurityCode()));
            try {
                List<Activity> activities = activityService.generateMockedActivities(account, 6);
                account.setActivities(activities);
                repository.save(account);
                return new CommonResult().generateOK("New account created.",null);
            }catch (Exception e){
                e.printStackTrace();
                return new CommonResult().generateBadRequest("Create new account failed!",null);
            }
        }
    }

    @GetMapping("/account/riderId/{riderId}")
    @ApiOperation(value = "Search existing account", notes = "Search existing account")
    public CommonResult<Account> searchExistingAccount(@PathVariable("riderId") String riderId) {
        Optional<Account> optional = repository.findByRiderId(riderId);
        if(optional.isPresent()){
            return new CommonResult<Account>().generateOK(null,optional.get());
        }else {
            return new CommonResult<Account>().generateBadRequest("Can't find this account!", null);
        }
    }

    @GetMapping("/account/info")
    public CommonResult<AccountInfo> getAccountInfo(@RequestHeader("Token") String token){
        Account account = activityService.getAccountByToken(token);
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setFirstName(account.getFirstName());
        accountInfo.setLastName(account.getLastName());
        accountInfo.setEmail(account.getEmail());
        accountInfo.setPhone(account.getPhone());
        accountInfo.setRiderId(account.getRiderId());
        accountInfo.setAccountType(account.getAccountType());
        List<Area> areas = areaRepository.findByAreaNameContainingIgnoreCase(account.getAreaName());
        Optional<Area> first = areas.stream().findFirst();
        if(first.isPresent()){
            accountInfo.setArea(first.get());
        }
        return new CommonResult<AccountInfo>().generateOK(null,accountInfo);
    }

    @PostMapping("/account")
    @ApiOperation(value = "Update existing account", notes = "Update existing account")
    public CommonResult updateExistingAccount(@RequestBody Account account) {
        String riderId = account.getRiderId();
        if (riderId == null || riderId.isEmpty()) {
            return new CommonResult<>().generateBadRequest("Update operation should provide Rider ID!",null);
        } else {
            Optional<Account> optional = repository.findByRiderId(riderId);
            if (optional.isPresent()) {
                Account existingAccount = optional.get();
                existingAccount.compareAndFillFields(account);
                repository.save(existingAccount);
                return new CommonResult<>().generateOK("Update operation completed!",null);
            } else {
                return new CommonResult<>().generateBadRequest("Can't find Account with provided rider ID!",null);
            }
        }
    }

    @GetMapping("/accounts")
    @ApiOperation(value = "Search rider IDs of existing accounts", notes = "Search rider IDs of existing accounts")
    public CommonResult<List<String>> searchExistingAccounts() {
        long count = repository.count();
        if (count > 0) {
            Iterable<Account> iterable = repository.findAll();
            Iterator<Account> iterator = iterable.iterator();
            List<String> ids = new ArrayList<>();
            while (iterator.hasNext()) {
                ids.add(iterator.next().getRiderId());
            }
            return new CommonResult<List<String>>().generateOK(null,ids);
        } else {
            return new CommonResult<List<String>>().generateBadRequest("No account exists!",null);
        }
    }
}
