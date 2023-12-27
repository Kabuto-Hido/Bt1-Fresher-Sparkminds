package com.bt1.qltv1.util;

import com.bt1.qltv1.config.JwtConfig;
import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.exception.TokenException;
import com.bt1.qltv1.service.SessionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Log4j
public class JwtUtil {
    private final JwtConfig jwtConfig;

    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String extractJTi(String token) {
        return extractClaim(token, Claims::getId);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public LocalDateTime extractExpiration(String token) {
        Date timeInDate = extractClaim(token, Claims::getExpiration);
        log.info("expired date: " + LocalDateTime
                .ofInstant(timeInDate.toInstant(), ZoneId.systemDefault()));
        return LocalDateTime
                .ofInstant(timeInDate.toInstant(), ZoneId.systemDefault());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        LocalDateTime now = LocalDateTime.now();
        log.info("now " + now);
        return extractExpiration(token).isBefore(now);
    }

    public String generateToken(String email, String jti) {
        Map<String, Object> claims = new HashMap<>();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, jwtConfig.getTokenExpirationAfterDays());
        Date expDate = c.getTime();

        return createToken(claims, email, jti, expDate);
    }

    public String generateRefreshToken(String email, String jti) {
        Map<String, Object> claims = new HashMap<>();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, jwtConfig.getRefreshTokenExpirationAfterDays());
        Date expDate = c.getTime();

        return createToken(claims, email, jti, expDate);
    }

    public String generateEmailToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createEmailToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject, String jti, Date expDate) {

        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expDate)
                .setId(jti)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey()).compact();
    }

    private String createEmailToken(Map<String, Object> claims, String subject) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, jwtConfig.getTokenExpirationAfterMinutes());
        Date expDate = c.getTime();
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expDate)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey()).compact();
    }

    //check token belong to user
    public void validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        //check token is expired
        if (Boolean.TRUE.equals(isTokenExpired(token))) {
            throw new TokenException("Your token is expired. Please login again.","token.expired");
        }
        //check token is belong to user
        if (!username.equals(userDetails.getUsername())) {
            throw new TokenException("Token is invalid!","token.invalid");
        }
    }
}
