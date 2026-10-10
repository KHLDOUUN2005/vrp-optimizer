package io.github.khaldouun2005.vrp.testutil;

import io.github.khaldouun2005.vrp.distance.HaversineDistance;
import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Route;
import io.github.khaldouun2005.vrp.model.TourMode;
import io.github.khaldouun2005.vrp.solver.RouteOptimizer;

public final class Benchmark {
	
	private Benchmark() {
	}
	
	public static BenchmarkResult run(RouteOptimizer optimizer, DistanceMatrix matrix, int depot, TourMode mode) {
		long t0 = System.nanoTime();
		Route route = optimizer.solve(matrix, depot, mode);
		long t1 = System.nanoTime();
		return new BenchmarkResult(route.totalDistance(), t1-t0);
	}
	
	public static BenchmarkResult runOnRandomInstance(RouteOptimizer optimizer, int size, long seed, TourMode mode) {
		DistanceMatrix matrix = InstanceGenerator.randomMatrix(size, seed, HaversineDistance.INSTANCE);
		return run(optimizer, matrix, 0, mode);
	}
}
