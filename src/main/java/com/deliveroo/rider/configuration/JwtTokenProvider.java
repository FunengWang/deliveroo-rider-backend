package com.deliveroo.rider.configuration;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.pojo.dto.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenProvider {
    private static final SecretKey SECRETKEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public TokenInfo generateToken(Account account) {
        String accountId = account.getId().toString();
        String riderId = account.getRiderId();
        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
        if (Boolean.TRUE.equals(connection.exists(riderId.getBytes()))) {
            String token = (String) redisTemplate.opsForValue().get(riderId);
            return new TokenInfo(token,account.getExpirationDate());
        } else {
            String token = Jwts.builder()
                    .setId(UUID.randomUUID().toString())
                    .setSubject("Subject")
                    .setIssuer("Issuer")
                    .setAudience("Audience")
                    .claim("accountId", accountId)
                    .claim("email", account.getEmail())
                    .setIssuedAt(issuedDate())
                    .setExpiration(expirationDate(account.getExpirationDate()))
                    .signWith(SECRETKEY)
                    .compact();
            long timeout = calculateTimeout(LocalDateTime.now(),account.getExpirationDate());
            redisTemplate.opsForValue().set(riderId,token,timeout, TimeUnit.SECONDS);
            TokenInfo tokenInfo = new TokenInfo(token,account.getExpirationDate());
            return tokenInfo;
        }
    }

    private long calculateTimeout(LocalDateTime from,LocalDateTime to){
        return Duration.between(from,to).getSeconds();
    }


    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRETKEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private  Date issuedDate() {
        return Date.from(LocalDateTime.now()
                .atZone(systemDefaultZone())
                .toInstant());
    }

    private  Date expirationDate(LocalDateTime expirationDate) {
        return Date.from(expirationDate
                .atZone(systemDefaultZone())
                .toInstant());
    }

    private ZoneId systemDefaultZone() {
        return ZoneId.systemDefault();
    }
}
