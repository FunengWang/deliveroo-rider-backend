package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.entity.Activity;
import com.deliveroo.rider.repository.AccountRepository;
import com.deliveroo.rider.service.ActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@Api(tags = "Account Administration")
public class AccountController {
    @Autowired
    private AccountRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ActivityService activityService;

    @PutMapping("/account")
    @ApiOperation(value = "Create new account", notes = "Create new account and add mocked activities this account")
    public ResponseEntity<String> createAccount(@RequestBody
                                                    @Validated Account account) {
        Optional<Account> optional = repository.findByRiderId(account.getRiderId());
        if (optional.isPresent()) {
            return ResponseEntity.badRequest().body("Account already existed. Can't create duplicate account!");
        } else {
            account.setSecurityCode(passwordEncoder.encode(account.getSecurityCode()));
            try {
                List<Activity> activities = activityService.generateMockedActivities(account, 6);
                account.setActivities(activities);
                repository.save(account);
                return ResponseEntity.ok("New account created.");
            }catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Create new account failed!");
            }
        }
    }

    @GetMapping("/account/{riderId}")
    @ApiOperation(value = "Search existing account", notes = "Search existing account")
    public ResponseEntity<Account> searchExistingAccount(@PathVariable("riderId") String riderId) {
        Optional<Account> optional = repository.findByRiderId(riderId);
        return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/account")
    @ApiOperation(value = "Update existing account", notes = "Update existing account")
    public ResponseEntity<String> updateExistingAccount(@RequestBody Account account) {
        String riderId = account.getRiderId();
        if (riderId == null || riderId.isEmpty()) {
            return ResponseEntity.badRequest().body("Update operation should provide Rider ID!");
        } else {
            Optional<Account> optional = repository.findByRiderId(riderId);
            if (optional.isPresent()) {
                Account existingAccount = optional.get();
                existingAccount.compareAndFillFields(account);
                Account saved = repository.save(existingAccount);
                return ResponseEntity.ok("Update operation completed!");
            } else {
                return ResponseEntity.badRequest().body("Can't find Account with provided rider ID!");
            }
        }
    }

    @GetMapping("/accounts")
    @ApiOperation(value = "Search rider IDs of existing accounts", notes = "Search rider IDs of existing accounts")
    public ResponseEntity<Object> searchExistingAccounts() {
        long count = repository.count();
        if (count > 0) {
            Iterable<Account> iterable = repository.findAll();
            Iterator<Account> iterator = iterable.iterator();
            List<String> ids = new ArrayList<>();
            while (iterator.hasNext()) {
                ids.add(iterator.next().getRiderId());
            }
            return ResponseEntity.ok(ids);
        } else {
            return ResponseEntity.badRequest().body("No Account exists!");
        }
    }
}
