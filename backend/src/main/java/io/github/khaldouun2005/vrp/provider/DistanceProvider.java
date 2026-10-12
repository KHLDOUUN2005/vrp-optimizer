package io.github.khaldouun2005.vrp.provider;

import java.util.List;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Stop;

public interface DistanceProvider {
	 DistanceMatrix computeMatrix(List<Stop> stops);
}
