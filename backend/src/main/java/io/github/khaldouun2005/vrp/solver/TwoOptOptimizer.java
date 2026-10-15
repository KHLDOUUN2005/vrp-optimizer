package io.github.khaldouun2005.vrp.solver;

import java.util.ArrayList;
import java.util.List;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Route;
import io.github.khaldouun2005.vrp.model.TourMode;

public class TwoOptOptimizer implements RouteOptimizer{

	@Override
	public Route solve(DistanceMatrix matrix, int depot, TourMode mode) {
		var solver = new NearestNeighborOptimizer();
		Route route = solver.solve(matrix, depot, mode);
		List<Integer> order = new ArrayList<>(route.order());
		int size = order.size();
		outer: for (int i=0; i<size-2; i++) {
			for (int j=i+2; j<size-1; j++) {
				int k=i, t=j;
				double dis1 = 0.0, dis2 = 0.0;
				while (k<=j) {
					dis1 += matrix.cost(order.get(k), order.get(k+1));
					k++;
				}
				dis2 += matrix.cost(order.get(i), order.get(j));
				while (t>i+1) {
					dis2 += matrix.cost(order.get(t), order.get(t-1));
					t--;
				}
				dis2 += matrix.cost(order.get(i+1), order.get(j+1));
				double delta = dis2 - dis1;
				if (delta<0) {
					reverseIt(order, i+1, j);
					i=-1;
					continue outer; 
				}
			}
			if (mode == TourMode.CLOSED) {
				int k=i, t=size-1;
				double dis1 = 0.0, dis2 = 0.0;
				while (k<size-1) {
					dis1 += matrix.cost(order.get(k), order.get(k+1));
					k++;
				}
				dis1 += matrix.cost(order.get(size-1), order.get(0));
				dis2 += matrix.cost(order.get(i), order.get(size-1));
				while (t>i+1) {
					dis2 += matrix.cost(order.get(t), order.get(t-1));
					t--;
				}
				dis2 += matrix.cost(order.get(i+1), order.get(0));
				double delta = dis2 - dis1;
				if (delta<0) {
					reverseIt(order, i+1, size-1);
					i=-1;
					continue outer; 
				}
			}
		}
		double total = 0.0;
		int before = depot;
		for (int el : order) {
			total += matrix.cost(before, el);
			before = el;
		}
		if (mode == TourMode.CLOSED) total += matrix.cost(before, depot);
		return new Route(order, total);
	}

	private void reverseIt(List<Integer> order, int i, int j) {
		while (i<j) {
			int eli = order.get(i);
			order.remove(i);
			order.add(i, order.get(j-1));
			order.remove(j);
			order.add(j, eli);
			i++;
			j--;
		}
	}
}
