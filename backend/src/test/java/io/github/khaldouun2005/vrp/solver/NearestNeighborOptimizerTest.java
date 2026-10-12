package io.github.khaldouun2005.vrp.solver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import io.github.khaldouun2005.vrp.distance.HaversineDistance;
import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Route;
import io.github.khaldouun2005.vrp.model.TourMode;
import io.github.khaldouun2005.vrp.testutil.Fixtures;
import io.github.khaldouun2005.vrp.testutil.InstanceGenerator;

public class NearestNeighborOptimizerTest {

	@Test
	public void returnsValidPermutation() {
		var solver = new NearestNeighborOptimizer();
		Route route = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);	
		assertThat(route.order().size()).isEqualTo(5);
		assertThat(route.order().get(0)).isEqualTo(0);
		var list = new HashSet<Integer>(route.order());
		assertThat(list.size()).isEqualTo(5);
	}
	
	@Test
	public void totalDistanceMatchesOrder() {
		var solver = new NearestNeighborOptimizer();
		DistanceMatrix disM = InstanceGenerator.randomMatrix(8, 42L, HaversineDistance.INSTANCE);
		Route route = solver.solve(disM, 0, TourMode.CLOSED);
		double distance = tourCost(route, disM, TourMode.CLOSED, 0);
		
		assertThat(route.totalDistance()).isEqualTo(distance, within(1e-9));
	}
	
	private static double tourCost(Route route, DistanceMatrix matrix, TourMode mode, int depot) {
		double distance = 0.0;
		int before = depot;
		for (int ele : route.order()) {
			distance += matrix.cost(before, ele);
			before = ele;
		}
		if (mode == TourMode.CLOSED) distance += matrix.cost(before, depot);
		return distance;
	}
	
	@Test
	public void neverBeatsBruteForce() {
		var solver = new NearestNeighborOptimizer();
		Route route = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);	
		assertThat(route.totalDistance())
		.isGreaterThanOrEqualTo(Fixtures.FIVE_STOP_CLOSED_OPTIMUM);
	}
	
	@Test
	public void openModeIsNotMoreExpensiveThanClosed() {
		var solver = new NearestNeighborOptimizer();
		Route routeC = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);
		Route routeO = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.OPEN);
		
		assertThat(routeC.totalDistance()).isGreaterThanOrEqualTo(routeO.totalDistance());
	}
	
	@Test
	public void deterministic() {
	    var solver = new NearestNeighborOptimizer();
	    Route first  = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);
	    Route second = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);
	    assertThat(first.order()).isEqualTo(second.order());
	    assertThat(first.totalDistance()).isEqualTo(second.totalDistance());
	}
	
	@Test
	public void modeDoesNotAffectTraversalOrder() {
	    var solver = new NearestNeighborOptimizer();
	    Route closed = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);
	    Route open   = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.OPEN);
	    assertThat(closed.order()).isEqualTo(open.order());
	}
	
	@Test
	public void scalesToLargeInstance() {
		var solver = new NearestNeighborOptimizer();
		DistanceMatrix disM = InstanceGenerator
				.randomMatrix(200, 42L, HaversineDistance.INSTANCE);
		Route route = solver.solve(disM, 0, TourMode.CLOSED);
		var hash = new HashSet<Integer>(route.order());
		
		assertThat(route.order().size()).isEqualTo(200);
		assertThat(hash.size()).isEqualTo(200);
		
	}
}
