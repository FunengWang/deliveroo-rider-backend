package com.deliveroo.rider.controller;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.pojo.dto.CommonResult;
import com.deliveroo.rider.pojo.dto.CustomUserDetails;
import com.deliveroo.rider.pojo.dto.LoginRequest;
import com.deliveroo.rider.configuration.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/auth")
    public CommonResult<String> login(@RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword());
        try{
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            Object principal = authenticate.getPrincipal();
            CustomUserDetails customUserDetails = (CustomUserDetails) principal;
            Account account = customUserDetails.getAccount();
            String token = tokenProvider.generateToken(account);
            return new CommonResult<String>().generateOK(null,token);
        }catch (Exception e){
            if(e instanceof AccountExpiredException){
                return new CommonResult<>(HttpStatus.UNAUTHORIZED.value(),"Account Expired!",null);
            }else {
                return new CommonResult<>(HttpStatus.UNAUTHORIZED.value(),null,null);
            }
        }
    }
}
