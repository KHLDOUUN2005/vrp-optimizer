package io.github.khaldouun2005.vrp.api.dto;

import java.util.List;
import io.github.khaldouun2005.vrp.model.TourMode;

public record OptimizeResponse(
    List<StopDto> orderedStops,
    List<double[]> geometry,
    double optimizedDistance,
    double naiveDistance,
    double savingsPercent,
    String providerUsed,
    String optimizerUsed,
    TourMode mode,
    long elapsedMs
) {}