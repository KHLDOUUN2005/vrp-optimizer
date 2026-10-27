package io.github.khaldouun2005.vrp.api.dto;

import java.time.Instant;

public record ErrorResponse(int status, String message, Instant timestamp) {}
