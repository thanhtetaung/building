package com.flextech.building.authentication;

import com.flextech.building.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTUtil {

    private static String jwtSecretKey;
    private static long expiration;

    @Value("${jwt.secret.key}")
    public void setJwtSecretKey(String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    @Value("${jwt.expiration}")
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    private static Key key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    public static String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("roleName", "ROLE_USER");
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuer("Zulu")
                .setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(expiration))))
                .setIssuedAt(Date.from(Instant.now()))
                .compact();
    }
}
