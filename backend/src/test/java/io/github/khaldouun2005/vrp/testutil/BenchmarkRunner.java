package io.github.khaldouun2005.vrp.testutil;

import java.util.List;

import io.github.khaldouun2005.vrp.model.TourMode;
import io.github.khaldouun2005.vrp.solver.NearestNeighborOptimizer;
import io.github.khaldouun2005.vrp.solver.RouteOptimizer;
import io.github.khaldouun2005.vrp.solver.TwoOptOptimizer;

public class BenchmarkRunner {
	
	public static double result(RouteOptimizer optimizer, int size, long seed, TourMode mode, String name) {
		Benchmark.runOnRandomInstance(optimizer, size, seed, mode); 
		var result = Benchmark.runOnRandomInstance(optimizer, size, seed, mode);
		double distance = result.totalDistance();
		double time = result.elapsedNanos()/1_000_000.0;
		System.out.printf("%d  %s        %.2f km    %.3f ms\n", size, name, distance, time);
		return distance;
	}
	
	public static void main(String...strings) {
		var twoOpt = new TwoOptOptimizer();
		var nnOptm = new NearestNeighborOptimizer();
		for (int size : List.of(10, 50, 200)) {
			System.out.println("===== "+size+" =====");
			double nnd = result(nnOptm, size, 42L, TourMode.CLOSED, "NN");
			double twooptd = result(twoOpt, size, 42L, TourMode.CLOSED, "NN+2-opt");
			double impr = (nnd - twooptd) / nnd * 100.0;
			System.out.printf("\n-Improvement : %.2f", impr);
			System.out.println("\n\n");
		}
	}
}
