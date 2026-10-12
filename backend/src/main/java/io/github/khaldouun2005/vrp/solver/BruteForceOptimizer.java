package io.github.khaldouun2005.vrp.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Route;
import io.github.khaldouun2005.vrp.model.TourMode;

public class BruteForceOptimizer implements RouteOptimizer{

	@Override
	public Route solve(DistanceMatrix matrix, int depot, TourMode mode){
		if (matrix.size() > 9) throw new IllegalArgumentException("the size of the matrix shouldn't pass 9!");
		List<List<Integer>> permutations = new ArrayList<>();
		List<Integer> remaining = new ArrayList<>();
		for (int i=0; i<matrix.size(); i++) {
			if (i != depot) remaining.add(i);
		}
		permutations(remaining, new ArrayList<Integer>(), list -> permutations.add(list));
		List<Integer> routeList = permutations.get(0);
		int n = routeList.size();
		double minn = getDistance(routeList, matrix, depot);
		if (mode == TourMode.CLOSED) minn += matrix.cost(routeList.get(n-1), depot);
		for (List<Integer> ls : permutations) {
			double distance = getDistance(ls, matrix, depot); 
			if (mode == TourMode.CLOSED) distance += matrix.cost(ls.get(n-1), depot);
			if (distance < minn) {
				minn = distance;
				routeList = ls; 
			}
		}
		List<Integer> finalOrder = new ArrayList<>();
		finalOrder.add(depot);
		finalOrder.addAll(routeList);
		return new Route(finalOrder, minn);
	}

	private double getDistance(List<Integer> ls, DistanceMatrix matrix, int depot) {
		double distance = 0.0;
		int before = depot;
		for (int i : ls) {
			distance += matrix.cost(before, i);
			before = i;
		}
		return distance;
	}

	public static void permutations(List<Integer> remaining,
            List<Integer> current,
            Consumer<List<Integer>> visitor) {
		if (remaining.isEmpty()) {
			visitor.accept(List.copyOf(current));
			return;
		}
		for (int i = 0; i < remaining.size(); i++) {
			int chosen = remaining.remove(i);
			current.add(chosen);
			permutations(remaining, current, visitor);
			current.remove(current.size() - 1);
			remaining.add(i, chosen);
		}
	}
	
}
