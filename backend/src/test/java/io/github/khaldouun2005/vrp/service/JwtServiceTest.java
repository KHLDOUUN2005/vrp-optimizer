package io.github.khaldouun2005.vrp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.JwtException;

public class JwtServiceTest {
    private JwtService service = new JwtService("a-very-long-and-random-secret-string-at-least-32-chars", 60);
    
    @Test 
    public void generateAndExtractRoundTrips() {
    	String token = service.generateToken("a@b.com");
    	String email = service.extractEmail(token);
    	
    	assertThat(email).isEqualTo("a@b.com");
    }
    
    @Test 
    public void extractRejectsTamperedToken() {
    	String token = service.generateToken("a@b.com");    	
    	assertThatThrownBy(() -> service.extractEmail(token+"/M"))
    	.isInstanceOf(JwtException.class);
    }
    
    @Test 
    public void extractRejectsExpiredToken() {
    	JwtService ser = new JwtService("a-very-long-and-random-secret-string-at-least-32-chars", 0);
    	String token = ser.generateToken("a@b.com");
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			 Thread.currentThread().interrupt();
		}
    	assertThatThrownBy(()-> ser.extractEmail(token))
    	.isInstanceOf(JwtException.class);
    }
}
