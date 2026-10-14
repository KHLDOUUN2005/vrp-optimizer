package io.github.khaldouun2005.vrp.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Stop;

public class OsrmDistanceProviderTest {
	private static final String OSRM_URL = "http://localhost:5000";
	private static final OsrmDistanceProvider provider = new OsrmDistanceProvider(OSRM_URL);
	private static final List<Stop> stops = List.of(
		    new Stop(0, "Casablanca Centre", 33.5731, -7.5898),
		    new Stop(1, "North",             33.6000, -7.6000),
		    new Stop(2, "East",              33.5797, -7.5503)
		);
	
	private static boolean osrmIsReachable() {
		try (Socket s = new Socket()){
			s.connect(new InetSocketAddress("localhost", 5000), 500);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	@Test
	public void computesCorrectSizeMatrix() {
		assumeTrue(osrmIsReachable(), "OSRM server not running on localhost:5000 — skipping");
		DistanceMatrix m = provider.computeMatrix(stops);
		assertThat(m.size()).isEqualTo(3);
	}
	
	@Test
	public void diagonalIsZero() {
		assumeTrue(osrmIsReachable(), "OSRM server not running on localhost:5000 — skipping");
		DistanceMatrix m = provider.computeMatrix(stops);
		for (int i=0; i<m.size(); i++) assertThat(m.cost(i, i)).isCloseTo(0.0,  within(1e-9));
	}
	
	@Test
	public void valuesAreInKilometres() {
		assumeTrue(osrmIsReachable(), "OSRM server not running on localhost:5000 — skipping");
		DistanceMatrix m = provider.computeMatrix(stops);
		assertThat(m.cost(0, 2)).isBetween(4.0, 6.0);
	}
	
	@Test
	public void isAsymmetricOnRealRoadNetwork() {
		assumeTrue(osrmIsReachable(), "OSRM server not running on localhost:5000 — skipping");
	    DistanceMatrix m = provider.computeMatrix(stops);

	    boolean anyAsymmetric =
	        m.cost(0, 1) != m.cost(1, 0) ||
	        m.cost(0, 2) != m.cost(2, 0) ||
	        m.cost(1, 2) != m.cost(2, 1);

	    assertThat(anyAsymmetric).isTrue();
	}
	
	@Test
	public void rejectsTooFewStops() {
		List<Stop> stops = List.of(new Stop(0, "solo", 33.5731, -7.5898));
		assertThatThrownBy(() -> provider.computeMatrix(stops))
		    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void throwsWhenServerUnreachable() {
	    OsrmDistanceProvider badProvider = new OsrmDistanceProvider("http://localhost:9999");
	    List<Stop> stops = List.of(
	        new Stop(0, "Casablanca", 33.5731, -7.5898),
	        new Stop(1, "North",      33.6000, -7.6000)
	    );

	    assertThatThrownBy(() -> badProvider.computeMatrix(stops))
	        .isInstanceOf(RuntimeException.class)
	        .hasMessageContaining("OSRM request failed");
	}

}
