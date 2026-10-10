package io.github.khaldouun2005.vrp.distance;

public enum HaversineDistance implements DistanceFunction{
	INSTANCE;
	
	private final double RADIUS = 6_371.0;
	@Override
	public double distance(double lat1, double lng1, double lat2, double lng2) {
		lat1 = Math.toRadians(lat1);
		lng1 = Math.toRadians(lng1);
		lat2 = Math.toRadians(lat2);
		lng2 = Math.toRadians(lng2);
		
		double dellat = lat2-lat1, dellong = lng2-lng1;
		double a = Math.pow(Math.sin(dellat/2), 2) + 
				Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dellong/2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double value = RADIUS * c;
		
		return value;
	}

}
