package io.github.khaldouun2005.vrp.solver;

import java.util.ArrayList;
import java.util.List;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Route;
import io.github.khaldouun2005.vrp.model.TourMode;

public class NearestNeighborOptimizer implements RouteOptimizer{

	@Override
	public Route solve(DistanceMatrix matrix, int depot, TourMode mode) {
		List<Integer> order = new ArrayList<>();
		order.add(depot);
		List<Integer> unvisited = new ArrayList<>();
		for (int i=0; i<matrix.size(); i++) {
			if (i != depot) unvisited.add(i);
		}
		int current = depot;
		double total = 0;
		while (!unvisited.isEmpty()) {
			int next = -1;
		    int indexNext = -1;
		    double minn = Double.POSITIVE_INFINITY;
			for (int i=0; i<unvisited.size(); i++) {
				double d = matrix.cost(current, unvisited.get(i));
				if (d<minn) {
					minn = d;
					next = unvisited.get(i);
					indexNext = i;
				}
			}
			unvisited.remove(indexNext);
			order.add(next);
			total += minn;
			current = next;
		}
		if (mode == TourMode.CLOSED) total += matrix.cost(current, depot);
		
		return new Route(order, total);
	}

}
