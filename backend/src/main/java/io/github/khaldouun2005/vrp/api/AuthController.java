package io.github.khaldouun2005.vrp.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.khaldouun2005.vrp.api.dto.LoginResponse;
import io.github.khaldouun2005.vrp.api.dto.RegisterRequest;
import io.github.khaldouun2005.vrp.api.dto.UserResponse;
import io.github.khaldouun2005.vrp.entity.AppUser;
import io.github.khaldouun2005.vrp.service.AuthService;
import io.github.khaldouun2005.vrp.service.JwtService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService appSer;
	private final JwtService jwtSer;
	
	public AuthController(AuthService appSer, JwtService jwtSer) {
		this.appSer = appSer;
		this.jwtSer = jwtSer;
	}
	
	@PostMapping("register")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse register(@Valid @RequestBody RegisterRequest creatUser) {
		AppUser user = appSer.register(creatUser.email(), creatUser.password());
		return new UserResponse(user.getId(), user.getEmail());
	}
	
	@PostMapping("login")
	public LoginResponse login(@Valid @RequestBody RegisterRequest creatUser) {
		AppUser user = appSer.login(creatUser.email(), creatUser.password());
		return new LoginResponse(jwtSer.generateToken(user.getEmail()));
	}
}
