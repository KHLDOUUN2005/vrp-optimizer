package io.github.khaldouun2005.vrp.solver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.HashSet;
import java.util.List;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import io.github.khaldouun2005.vrp.distance.HaversineDistance;
import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Route;
import io.github.khaldouun2005.vrp.model.TourMode;
import io.github.khaldouun2005.vrp.testutil.Fixtures;
import io.github.khaldouun2005.vrp.testutil.InstanceGenerator;

public class BruteForceOptimizerTest {
	
	@Test
	public void fixtureClosedModeFindsOptimum() {
		var solver = new BruteForceOptimizer();
		double distance = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED)
				.totalDistance();
		assertThat(distance).isCloseTo(Fixtures.FIVE_STOP_CLOSED_OPTIMUM, within(1e-9));
	}
	
	@Test
	public void fixtureOpenModeFindsOptimum() {
		var solver = new BruteForceOptimizer();
		double distance = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.OPEN)
				.totalDistance();
		assertThat(distance).isCloseTo(Fixtures.FIVE_STOP_OPEN_OPTIMUM, within(1e-9));
	}
	
	@Test
	public void returnsValidPermutation() {
		var solver = new BruteForceOptimizer();
		Route route = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.OPEN);	
		assertThat(route.order().size()).isEqualTo(5);
		assertThat(route.order().get(0)).isEqualTo(0);
		var list = new HashSet<Integer>(route.order());
		assertThat(list.size()).isEqualTo(5);
	}
	
	@Test
	public void rejectsMatrixLargerThanNine() {
		DistanceMatrix disM = InstanceGenerator.randomMatrix(10, 1L, HaversineDistance.INSTANCE);
		var solver = new BruteForceOptimizer();
		assertThatThrownBy(() -> solver.solve(disM, 0, TourMode.OPEN)).isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void acceptsMatrixOfSizeNine() {
		DistanceMatrix disM = InstanceGenerator.randomMatrix(9, 1L, HaversineDistance.INSTANCE);
		var solver = new BruteForceOptimizer();
		ThrowingCallable doIt = () -> solver.solve(disM, 0, TourMode.OPEN);
		assertThatCode(doIt).doesNotThrowAnyException();
	}
	
	@Test
	public void fixtureOptimalTourIsCorrectOrder() {
		var solver = new BruteForceOptimizer();
		List<Integer> opt1 = List.of(0,1,4,2,3), opt2 = List.of(0,3,2,4,1),
				order = solver.solve(Fixtures.fiveStopMatrix(), 0, TourMode.CLOSED).order();
		
		assertThat(order).isIn(opt1, opt2);
	}
}
