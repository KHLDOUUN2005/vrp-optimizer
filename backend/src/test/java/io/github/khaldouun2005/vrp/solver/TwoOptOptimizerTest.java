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

public class TwoOptOptimizerTest{
	@Test
	public void returnsValidPermutation() {
		var solver = new TwoOptOptimizer();
		Route route = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);	
		assertThat(route.order().size()).isEqualTo(5);
		assertThat(route.order().get(0)).isEqualTo(0);
		var list = new HashSet<Integer>(route.order());
		assertThat(list.size()).isEqualTo(5);
	}
	
	@Test
	public void totalDistanceMatchesOrder() {
		var solver = new TwoOptOptimizer();
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
	public void neverWorseThanStartingTour() {
		var nn = new NearestNeighborOptimizer();
		var twoOpt = new TwoOptOptimizer();
		DistanceMatrix disM = InstanceGenerator.randomMatrix(8, 42L, HaversineDistance.INSTANCE);
		Route routeNN = nn.solve(disM, 0, TourMode.CLOSED),
				routeOPT = twoOpt.solve(disM, 0, TourMode.CLOSED);
		assertThat(routeNN.totalDistance()).isGreaterThanOrEqualTo(routeOPT.totalDistance());
	}
	
	@Test
	public void neverBeatsBruteForce() {
		var solver = new TwoOptOptimizer();
		Route route = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);
		
		assertThat(route.totalDistance())
	    .isGreaterThanOrEqualTo(Fixtures.FIVE_STOP_CLOSED_OPTIMUM);
	}
	
	@Test
	public void matchesOptimumOnFixture() {
		var solver = new TwoOptOptimizer();
		Route route = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);
		
		assertThat(route.totalDistance())
		.isCloseTo(Fixtures.FIVE_STOP_CLOSED_OPTIMUM, within(1e-9));
	}
	
	@Test
	public void scalesToLargeInstance() {
		var solver = new TwoOptOptimizer();
		DistanceMatrix disM = InstanceGenerator
				.randomMatrix(200, 42L, HaversineDistance.INSTANCE);
		Route route = solver.solve(disM, 0, TourMode.CLOSED);
		var hash = new HashSet<Integer>(route.order());
		
		assertThat(route.order().size()).isEqualTo(200);
		assertThat(hash.size()).isEqualTo(200);
	}
	
	@Test
	public void deterministic() {
		var solver = new TwoOptOptimizer();
		Route first  = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);
	    Route second = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED);
	    assertThat(first.order()).isEqualTo(second.order());
	    assertThat(first.totalDistance()).isEqualTo(second.totalDistance());
	}
	
	@Test
	public void twoOptNeverWorsensOnAsymmetricMatrix() {
	    DistanceMatrix m = asymmetricFixture();
	    Route nn = new NearestNeighborOptimizer().solve(m, 0, TourMode.CLOSED);
	    Route twoOpt = new TwoOptOptimizer().solve(m, 0, TourMode.CLOSED);

	    assertThat(twoOpt.totalDistance()).isLessThanOrEqualTo(nn.totalDistance());
	}
	
	private static DistanceMatrix asymmetricFixture() {
	    double[][] g = {
	        { 0, 4, 9, 7, 6 },
	        { 3, 0, 5, 8, 4 },
	        { 8, 6, 0, 3, 9 },
	        { 7, 9, 2, 0, 5 },
	        { 5, 3, 7, 6, 0 }
	    };
	    return DistanceMatrix.fromGrid(g);
	}
	
}
