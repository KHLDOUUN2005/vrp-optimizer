package io.github.khaldouun2005.vrp.api;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.khaldouun2005.vrp.api.dto.ErrorResponse;
import io.github.khaldouun2005.vrp.api.dto.LoginResponse;
import io.github.khaldouun2005.vrp.api.dto.RegisterRequest;
import io.github.khaldouun2005.vrp.api.dto.UserResponse;
import io.github.khaldouun2005.vrp.entity.AppUser;
import io.github.khaldouun2005.vrp.service.AuthService;
import io.github.khaldouun2005.vrp.service.JwtService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
@RestControllerAdvice
public class AuthController {
	private final AuthService appSer;
	private final JwtService jwtSer;
	
	public AuthController(AuthService appSer, JwtService jwtSer) {
		this.appSer = appSer;
		this.jwtSer = jwtSer;
	}
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex){
		ErrorResponse error = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				ex.getMessage(),
				Instant.now()
				);
		return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodArgument(MethodArgumentNotValidException ex){
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(
				(error) ->{
					errors.put(error.getField(), error.getDefaultMessage());
				});
		return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAll(Exception ex){
		System.err.println("An unexpected error occurred.\n"+ ex.getMessage());
		ErrorResponse error = new ErrorResponse(
				500,
				"An unexpected error occurred.",
				Instant.now()
				);
		return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
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
