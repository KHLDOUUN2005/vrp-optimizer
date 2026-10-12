package io.github.khaldouun2005.vrp.provider;

import java.util.List;

import io.github.khaldouun2005.vrp.distance.HaversineDistance;
import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Stop;

public class HaversineDistanceProvider implements DistanceProvider{
	@Override
	public DistanceMatrix computeMatrix(List<Stop> stops) {
		return DistanceMatrix.fromStops(stops, HaversineDistance.INSTANCE);
	}

}
