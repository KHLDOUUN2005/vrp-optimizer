package io.github.khaldouun2005.vrp.solver;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Route;
import io.github.khaldouun2005.vrp.model.TourMode;

@FunctionalInterface
public interface RouteOptimizer {
	Route solve(DistanceMatrix matrix, int depot, TourMode mode);
}
