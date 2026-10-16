package io.github.khaldouun2005.vrp.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.khaldouun2005.vrp.entity.AppUser;
import io.github.khaldouun2005.vrp.repository.AppUserRepository;

@Service
public class AuthService {
	
	private final AppUserRepository appUserRep;
	private final PasswordEncoder passEncd;

	public AuthService(AppUserRepository appUserRep, PasswordEncoder passEncd) {
	    this.appUserRep = appUserRep;
	    this.passEncd = passEncd;
	}
	
	public AppUser register(String email, String password) {
		if (appUserRep.existsByEmail(email)) throw new IllegalArgumentException("email already registered!");
		AppUser appUser = new AppUser(email, passEncd.encode(password), Instant.now());
		appUserRep.save(appUser);
		return appUser;
	}
	
	public AppUser login(String email, String password) {
	    AppUser user = appUserRep.findByEmail(email)
	            .orElseThrow(() -> new IllegalArgumentException("invalid credentials"));
	    if (!passEncd.matches(password, user.getPasswordHash()))
	        throw new IllegalArgumentException("invalid credentials");
	    return user;
	}
}
