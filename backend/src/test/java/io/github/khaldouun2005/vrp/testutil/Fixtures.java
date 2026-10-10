package io.github.khaldouun2005.vrp.testutil;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;

/**
 * Hand-built TSP instances with hand-verified optimal tours.
 *
 * <p>The primary fixture is {@link #fiveStopMatrix()}: 5 stops in a 2D plane,
 * laid out as three corners of a unit square plus one extra point to the
 * right. The geometry is deliberately irregular — not a perfect square, not
 * collinear — so that the optimal tour is unique up to reversal, and
 * distinguishing a good algorithm from a bad one is possible.
 *
 * <p>The optima are documented below and were verified by hand-enumerating
 * all 24 permutations of the four non-depot stops.
 *
 * <pre>
 *      (0,1) 3          (1,1) 2
 *         +----------------+
 *         |                |
 *         |                |
 *         +----------------+----+
 *      (0,0) 0           (1,0) 1  (2,0) 4
 *       depot
 * </pre>
 */
public final class Fixtures {

    /**
     * Optimal closed-tour distance for {@link #fiveStopMatrix()}.
     * Achieved by 0 → 1 → 4 → 2 → 3 → 0 (or its reverse):
     * 1 + 1 + sqrt(2) + 1 + 1 = 4 + sqrt(2).
     */
    public static final double FIVE_STOP_CLOSED_OPTIMUM = 4 + Math.sqrt(2);

    /**
     * Optimal open-tour distance for {@link #fiveStopMatrix()}.
     * Achieved by 0 → 3 → 2 → 1 → 4 (or its reverse):
     * 1 + 1 + 1 + 1 = 4.
     */
    public static final double FIVE_STOP_OPEN_OPTIMUM = 4.0;

    /** Number of stops (depot + deliveries) in {@link #fiveStopMatrix()}. */
    public static final int FIVE_STOP_COUNT = 5;

    private Fixtures() {
    }

    /**
     * Returns the 5-stop Euclidean distance matrix for the fixture.
     * Stop 0 is the depot. Stop indices correspond to the diagram above.
     */
    public static DistanceMatrix fiveStopMatrix() {
        double sqrt2 = Math.sqrt(2), sqrt5 = Math.sqrt(5);
        double[][] grid = {
                {0, 1, sqrt2, 1, 2},
                {1, 0, 1, sqrt2, 1},
                {sqrt2, 1, 0, 1, sqrt2},
                {1, sqrt2, 1, 0, sqrt5},
                {2, 1, sqrt2, sqrt5, 0}
        };
        return DistanceMatrix.fromGrid(grid);
    }
}