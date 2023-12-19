package com.bt1.qltv1.util;

import com.bt1.qltv1.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {
    @Autowired
    private JwtConfig jwtConfig;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, jwtConfig.getTokenExpirationAfterDays());
        Date expDate = c.getTime();

        return createToken(claims, email, expDate);
    }

    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, jwtConfig.getRefreshTokenExpirationAfterDays());
        Date expDate = c.getTime();

        return createToken(claims, email, expDate);
    }

//    public String generateEmailToken(String username) {
//        Map<String, Object> claims = new HashMap<>();
//        return createEmailToken(claims, username);
//    }

    private String createToken(Map<String, Object> claims, String subject, Date expDate) {

        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expDate)
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

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }
}
