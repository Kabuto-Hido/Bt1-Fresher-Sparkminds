package com.bt1.qltv1.util;

import com.bt1.qltv1.config.JwtConfig;
import com.bt1.qltv1.entity.Session;
import com.bt1.qltv1.exception.TokenException;
import com.bt1.qltv1.service.SessionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
public class JwtUtil {
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private SessionService sessionService;

    public String extractJTi(String token) {
        return extractClaim(token, Claims::getId);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public LocalDateTime extractExpiration(String token) {
        Date timeInDate = extractClaim(token, Claims::getExpiration);
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

//    public String generateEmailToken(String username) {
//        Map<String, Object> claims = new HashMap<>();
//        return createEmailToken(claims, username);
//    }

    private String createToken(Map<String, Object> claims, String subject, String jti, Date expDate) {

        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expDate)
                .setId(jti)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey()).compact();
    }

//    private String createEmailToken(Map<String, Object> claims, String subject) {
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.MINUTE, jwtConfig.getTokenExpirationAfterMinutes());
//        Date expDate = c.getTime();
//        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(expDate)
//                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey()).compact();
//    }

    //check token belong to user
    public void validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        if (Boolean.FALSE.equals(isTokenExpired(token))) {
            throw new TokenException("Your token is expired. Please login again.");
        }

        if (username.equals(userDetails.getUsername())) {
            throw new TokenException("Token is invalid!");
        }

        //find session from refresh token
        String jti = extractJTi(token);
        Session session = sessionService.findByJti(jti);
        if (session.isBlock()) {
            throw new TokenException("Your refresh token can not use any more.");
        }

    }
}
