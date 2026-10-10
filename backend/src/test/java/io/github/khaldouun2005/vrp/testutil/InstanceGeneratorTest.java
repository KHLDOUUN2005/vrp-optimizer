package io.github.khaldouun2005.vrp.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.khaldouun2005.vrp.distance.HaversineDistance;
import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Stop;

public class InstanceGeneratorTest {
	@Test
	public void sameSeedProducesIdenticalStops() {
		assertThat(InstanceGenerator.randomStops(10, 42L)).isEqualTo(InstanceGenerator.randomStops(10, 42L));
	}
	
	@Test
	public void differentSeedsProduceDifferentStops() {
		assertThat(InstanceGenerator.randomStops(10, 1L)).isNotEqualTo(InstanceGenerator.randomStops(10, 2L));
	}
	
	@Test
	public void randomStopsReturnsRequestedCount() {
		assertThat(InstanceGenerator.randomStops(7, 1L)).hasSize(7);
	}
	
	@Test
	public void stopIdsMatchTheirPositions() {
		List<Stop> stops = InstanceGenerator.randomStops(10, 0L);
		for (int i=0; i<10; i++) {
			assertThat(stops.get(i).id()).isEqualTo(i);
		}
	}
	
	@Test
	public void coordinatesStayInsideBoundingBox() {
		List<Stop> stops = InstanceGenerator.randomStops(10, 0L);
		
		for (Stop stop : stops) {
			assertThat(stop.latitude()).isBetween(33.32, 33.82);
			assertThat(stop.longitude()).isBetween(-7.84, -7.34);
		}
	}
	
	@Test
	public void randomMatrixBuildsFromStops() {
		DistanceMatrix disM = InstanceGenerator.randomMatrix(10, 42L, HaversineDistance.INSTANCE);
		List<Stop> stops = InstanceGenerator.randomStops(10, 42L);
		assertThat(disM.size()).isEqualTo(10);
		Stop stop1 = stops.get(0), stop2 = stops.get(1);
		assertThat(disM.cost(0, 1)).isEqualTo(HaversineDistance.INSTANCE.distance(
				stop1.latitude(), stop1.longitude(), 
				stop2.latitude(), stop2.longitude()));
	}
}
