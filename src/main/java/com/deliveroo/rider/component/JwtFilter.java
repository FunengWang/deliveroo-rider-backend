package com.deliveroo.rider.component;

import com.alibaba.fastjson.JSON;
import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.pojo.dto.CommonResult;
import com.deliveroo.rider.pojo.dto.CustomUserDetails;
import com.deliveroo.rider.configuration.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public static final String TOKEN_HEADER = "Token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(TOKEN_HEADER);
        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = tokenProvider.parseToken(token);
            String email = claims.get("email", String.class);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (userDetails instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                Account account = customUserDetails.getAccount();
                if (account.getEmail().equals(email)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            if (e instanceof ExpiredJwtException) {
                response.getWriter().println(JSON.toJSONString(new CommonResult<>(HttpStatus.UNAUTHORIZED.value(), "Token Expired",null)));
            } else {
                response.getWriter().println(JSON.toJSONString(new CommonResult<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",null)));
            }
            return;
        }

        filterChain.doFilter(request, response);
    }
}
