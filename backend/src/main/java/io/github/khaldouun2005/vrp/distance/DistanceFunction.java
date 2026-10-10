package io.github.khaldouun2005.vrp.distance;

@FunctionalInterface
public interface DistanceFunction {
	double distance(double lat1, double lng1, double lat2, double lng2);
}
