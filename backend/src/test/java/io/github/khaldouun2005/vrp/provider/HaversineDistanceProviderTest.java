package io.github.khaldouun2005.vrp.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.khaldouun2005.vrp.distance.HaversineDistance;
import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Stop;
import io.github.khaldouun2005.vrp.testutil.InstanceGenerator;

public class HaversineDistanceProviderTest {
	@Test
	public void computesCorrectSizeMatrix () {
		var provider = new HaversineDistanceProvider();
		List<Stop> stops = InstanceGenerator.randomStops(10, 0L);
		DistanceMatrix disM = provider.computeMatrix(stops);
		
		assertThat(stops.size()).isEqualTo(disM.size());
	}
	
	@Test
	public void valuesMatchDirectHaversine () {
		var provider = new HaversineDistanceProvider();
		List<Stop> stops = InstanceGenerator.randomStops(10, 0L);
		DistanceMatrix disM = provider.computeMatrix(stops);
		Stop stop1 = stops.get(0), stop2 = stops.get(7);
		assertThat(disM.cost(0, 7)).isEqualTo(HaversineDistance.INSTANCE.distance(
				stop1.latitude(), stop1.longitude(),
				stop2.latitude(), stop2.longitude()));
	}
	
	@Test
	public void diagonalIsZero () {
		var provider = new HaversineDistanceProvider();
		List<Stop> stops = InstanceGenerator.randomStops(10, 0L);
		DistanceMatrix disM = provider.computeMatrix(stops);
		for (int i=0; i<disM.size(); i++) {
			assertThat(disM.cost(i, i)).isEqualTo(0);
		}
	}
	
	@Test
	public void  rejectsNullStops () {
		var provider = new HaversineDistanceProvider();
		List<Stop> stops = new ArrayList<>();
		stops.add(new Stop(0, "s0", 0, 0));
		stops.add(new Stop(1, "s1", 1, 1));
		stops.add(null);
		stops.add(new Stop(2, "s2", 2, 2));
		assertThatThrownBy(() -> provider.computeMatrix(stops))
		.isInstanceOf(NullPointerException.class);
	}
	
	@Test
	public void rejectsTooFewStops () {
		var provider = new HaversineDistanceProvider();
		List<Stop> stops = List.of(new Stop(0, "s0",0,0));
		assertThatThrownBy(() -> provider.computeMatrix(stops))
		.isInstanceOf(IllegalArgumentException.class);
	}
}
