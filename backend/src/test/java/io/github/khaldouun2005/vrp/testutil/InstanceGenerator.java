package io.github.khaldouun2005.vrp.testutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.khaldouun2005.vrp.distance.DistanceFunction;
import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Stop;

public class InstanceGenerator {
	private static final double MIN_LAT = 33.32;
	private static final double MAX_LAT = 33.82;
	private static final double MIN_LNG = -7.84;
	private static final double MAX_LNG = -7.34;
	
	
	public static List<Stop> randomStops(int count, long seed){
		List<Stop> stops = new ArrayList<>();
		Random rand = new Random(seed);
		for (int i=0; i<count; i++) {
			double lat = MIN_LAT + rand.nextDouble() * (MAX_LAT - MIN_LAT);
			double lng = MIN_LNG + rand.nextDouble() * (MAX_LNG - MIN_LNG);
			stops.add(new Stop(i, "Stop "+i, lat, lng));
		}
		return stops;
	}
	
	public static DistanceMatrix randomMatrix(int count, long seed, DistanceFunction distanceFunction) {
		return DistanceMatrix.fromStops(randomStops(count, seed), distanceFunction);
	}
}
