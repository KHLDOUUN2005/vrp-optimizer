package io.github.khaldouun2005.vrp.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	 private final SecretKey key;
	    private final long expirationMs;

	    public JwtService(@Value("${jwt.secret}") String secret,
	                      @Value("${jwt.expiration-minutes}") long expirationMinutes) {
	        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	        this.expirationMs = expirationMinutes * 60_000L;
	    }

    public String generateToken(String email) {
    	return Jwts.builder()
    			.subject(email)
    			.issuedAt(new Date())
    			.expiration(new Date(System.currentTimeMillis() + expirationMs))
    			.signWith(key)
    			.compact();
    }
    public String extractEmail(String token) {
    	return Jwts.parser()
    			.verifyWith(key)
    			.build()
    			.parseSignedClaims(token)
    			.getPayload()
    			.getSubject();
    }
}