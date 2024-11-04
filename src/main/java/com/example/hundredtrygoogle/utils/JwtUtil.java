package com.example.hundredtrygoogle.utils;

import com.example.hundredtrygoogle.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "843n7ct39cn4apn8ap4e98tnqp48vt7qp49n8vqp489tqn3p98tvqn3p498tvpqq";
    private static final String REFRESH_SECRET_KEY = "oiunrc20un3r32uncrw09nuraupmaosdu0239r029unt2039m09kfs0d923309nu";

    private static final long ACCESS_TOKEN_EXPIRATION = 3600000;
    private static final long REFRESH_TOKEN_EXPIRATION = 86400000; // 24 hours

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, SECRET_KEY); // Default to access token secret
    }

    public String extractRefreshUsername(String token) {
        return extractClaim(token, Claims::getSubject, REFRESH_SECRET_KEY); // Default to access token secret
    }

    // Validate access token
    public boolean isAccessTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !checkTokenExpired(token, SECRET_KEY);
    }

    // Validate refresh token
    public boolean isRefreshTokenValid(String token, User user) {
        final String username = extractClaim(token, Claims::getSubject, REFRESH_SECRET_KEY);
        return (username.equals(user.getUsername())) && !checkTokenExpired(token, REFRESH_SECRET_KEY);
    }

//    accesstoken
    public boolean isTokenExpired(String token){
        return checkTokenExpired(token, SECRET_KEY);
    }

    public boolean isRefreshTokenExpired(String token){
        return checkTokenExpired(token, REFRESH_SECRET_KEY);
    }

    // Check if token is expired (for both access and refresh tokens)
    public boolean checkTokenExpired(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey).before(new Date());
    }

    // Generate access token
    public String generateAccessToken(User user) {
        return buildToken(user, SECRET_KEY, ACCESS_TOKEN_EXPIRATION);
    }

    // Generate refresh token
    public String generateRefreshToken(User user) {
        return buildToken(user, REFRESH_SECRET_KEY, REFRESH_TOKEN_EXPIRATION);
    }

    // Generic method to build token (used for both access and refresh tokens)
    private String buildToken(User user, String secretKey, long expirationTime) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract all claims
    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract specific claim from token (for access or refresh tokens)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    // Get signing key based on the secret key (for access or refresh tokens)
    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

