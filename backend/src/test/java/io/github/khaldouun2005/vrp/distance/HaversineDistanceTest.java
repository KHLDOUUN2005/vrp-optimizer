package io.github.khaldouun2005.vrp.distance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

public class HaversineDistanceTest {
	@Test
	public void parisToLondonIsAbout344Km() {
		assertThat(HaversineDistance.INSTANCE.distance(48.8566, 2.3522, 51.5074, -0.1278))
		.isCloseTo(344.0, within(1.0));
	}
	
	@Test
	public void distanceIsSymmetric() {
		double a = HaversineDistance.INSTANCE.distance(10.0, 20.0, 30.0, 40.0);
		double b = HaversineDistance.INSTANCE.distance(30.0, 40.0, 10.0, 20.0);
		assertThat(a).isCloseTo(b, within(1e-9));
	}
	
	@Test
	public void distanceFromPointToItselfIsZero() {
		double a = HaversineDistance.INSTANCE.distance(33.57, -7.59, 33.57, -7.59);
		assertThat(a).isEqualTo(0.0);
	}
	
	@Test
	public void poleToPoleIsHalfCircumference() {
	    double a = HaversineDistance.INSTANCE.distance(90, 0, -90, 0);
	    assertThat(a).isCloseTo(20015.0, within(5.0)); 
	}
	
	@Test
	public void oneThousandthOfDegreeIsAbout111Meters() {
		double a = HaversineDistance.INSTANCE.distance(0, 0, 0.001, 0);
		assertThat(a).isCloseTo(0.111, within(0.005));
	}
	
	
}
