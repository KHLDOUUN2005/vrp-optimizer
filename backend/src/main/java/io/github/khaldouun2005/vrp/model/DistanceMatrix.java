package io.github.khaldouun2005.vrp.model;

import io.github.khaldouun2005.vrp.distance.DistanceFunction;

import java.util.List;
import java.util.Objects;

public final class DistanceMatrix {

    private final double[][] grid;

    
    private DistanceMatrix(double[][] source) {
        double[][] copy = deepCopy(source);   
        validate(copy);                      
        this.grid = copy;
    }

    public static DistanceMatrix fromGrid(double[][] grid) {
        return new DistanceMatrix(grid);
    }

    public static DistanceMatrix fromStops(List<Stop> stops, DistanceFunction distanceFunction) {
        Objects.requireNonNull(stops, "stops must not be null");
        Objects.requireNonNull(distanceFunction, "distanceFunction must not be null");

        int n = stops.size();
        double[][] raw = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Stop a = stops.get(i);
                Stop b = stops.get(j);
                raw[i][j] = distanceFunction.distance(
                        a.latitude(), a.longitude(),
                        b.latitude(), b.longitude());
            }
        }
        return new DistanceMatrix(raw);       
    }

    public int size() {
        return grid.length;
    }

    public double cost(int from, int to) {
        Objects.checkIndex(from, grid.length);
        Objects.checkIndex(to, grid.length);
        return grid[from][to];
    }

    private static double[][] deepCopy(double[][] source) {
        if (source == null) {
            throw new IllegalArgumentException("grid must not be null");
        }
        double[][] copy = new double[source.length][];
        for (int i = 0; i < source.length; i++) {
            if (source[i] == null) {
                throw new IllegalArgumentException("row " + i + " is null");
            }
            copy[i] = source[i].clone();      
        }
        return copy;
    }

    private static void validate(double[][] g) {
        int n = g.length;
        if (n < 2) {
            throw new IllegalArgumentException("grid must contain at least two stops (depot + one delivery), got " + n);
        }
        for (int i = 0; i < n; i++) {
            if (g[i].length != n) {
                throw new IllegalArgumentException(
                        "grid must be square: row " + i + " has " + g[i].length
                        + " entries, expected " + n);
            }
            for (int j = 0; j < n; j++) {
                double v = g[i][j];
                if (Double.isNaN(v) || Double.isInfinite(v)) {
                    throw new IllegalArgumentException(
                            "entry [" + i + "][" + j + "] is not a finite number: " + v);
                }
                if (v < 0) {
                    throw new IllegalArgumentException(
                            "entry [" + i + "][" + j + "] is negative: " + v);
                }
            }
            if (g[i][i] != 0) {
                throw new IllegalArgumentException(
                        "diagonal entry [" + i + "][" + i + "] must be 0, got " + g[i][i]);
            }
        }
    }
}