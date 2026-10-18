package io.github.khaldouun2005.vrp.api.dto;

import java.util.List;
import io.github.khaldouun2005.vrp.model.TourMode;
import jakarta.validation.constraints.NotEmpty;

public record OptimizeRequest(
    @NotEmpty List<StopDto> stops,
    int depotIndex,
    TourMode mode,
    String provider,   
    String optimizer   ) {}