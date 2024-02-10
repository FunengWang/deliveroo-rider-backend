package com.deliveroo.rider.util;

import com.deliveroo.rider.entity.Account;
import com.deliveroo.rider.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtil {
    private static final SecretKey SECRETKEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    public static Map<String, String> TOKEN_MAP = new HashMap<>();

    @Autowired
    private RedisService redisService;

    public static String generateToken(Account account) {
        String riderId = account.getRiderId();
        if (TOKEN_MAP.containsKey(riderId)) {
            return TOKEN_MAP.get(riderId);
        } else {
            String token = Jwts.builder()
                    .setId(UUID.randomUUID().toString())
                    .setSubject("Subject")
                    .setIssuer("Issuer")
                    .setAudience("Audience")
                    .claim("riderId", riderId)
                    .claim("email", account.getEmail())
                    .setIssuedAt(issuedDate())
                    .setExpiration(expirationDate(account.getExpirationDate()))
                    .signWith(SECRETKEY)
                    .compact();
            TOKEN_MAP.put(riderId, token);
            return token;
        }
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRETKEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private static Date issuedDate() {
        return Date.from(LocalDateTime.now()
                .atZone(systemDefaultZone())
                .toInstant());
    }

    private static Date expirationDate(LocalDateTime expirationDate) {
        return Date.from(expirationDate
                .atZone(systemDefaultZone())
                .toInstant());
    }

    private static ZoneId systemDefaultZone() {
        return ZoneId.systemDefault();
    }

    private static void clearExpiredToken(String token) {
        TOKEN_MAP.entrySet().removeIf(entry -> entry.getValue().equals(token));
    }
}
