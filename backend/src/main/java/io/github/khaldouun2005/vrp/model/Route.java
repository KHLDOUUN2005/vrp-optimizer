package io.github.khaldouun2005.vrp.model;

import java.util.HashSet;
import java.util.List;

public record Route(List<Integer> order, double totalDistance) {
	public Route{
		if (order == null || order.size()<2) throw new IllegalArgumentException("the list can't be null or has less than two elements !!");
		order = List.copyOf(order);
		int n = order.size();
		if (new HashSet<>(order).size() != n) throw new IllegalArgumentException("the list should not has a duplicate elements!!");
		for (int v : order) {
			if (v < 0) throw new IllegalArgumentException("the list should not have a negative element !!");
		}
		if (!Double.isFinite(totalDistance) || totalDistance<0) throw new IllegalArgumentException("Total is not valid!");
	}
}
