package io.github.khaldouun2005.vrp.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
	    @NotBlank String email,
	    @NotBlank String password
	) {}
