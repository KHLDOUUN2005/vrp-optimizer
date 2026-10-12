# Benchmarks

First quantitative results for the TSP optimizers on random Euclidean instances.

## Setup

- Instance source: `InstanceGenerator.randomMatrix`, Haversine distances
- Bounding box: Casablanca region (0.5° × 0.5°)
- Mode: CLOSED tour
- Seed: 42 (fixed, same for both algorithms at each size)
- Warm-up: one untimed invocation before each timed run
- JVM: Java 21 (Temurin)
- Machine: Intel(R) Core(TM) i5-6200U CPU @ 2.30GHz  "on Windows 11"
- Measurement: single run per configuration, `System.nanoTime()`

## Results

| Stops | Algorithm  | Total distance (km) | Runtime (ms) | Improvement vs NN |
|-------|------------|---------------------|--------------|-------------------|
| 10    | NN         | 115.60              | 0.118        | —                 |
| 10    | NN + 2-opt | 115.60              | 0.196        | 0.00 %            |
| 50    | NN         | 359.79              | 0.750        | —                 |
| 50    | NN + 2-opt | 310.03              | 5.332        | 13.83 %           |
| 200   | NN         | 671.52              | 6.441        | —                 |
| 200   | NN + 2-opt | 554.03              | 62.547       | 17.50 %           |

## Observations

- **Nearest Neighbor is fast but suboptimal.** It scales quadratically
  with the number of stops and finishes 200-stop instances in a few
  milliseconds.

- **2-opt trades time for distance.** At 50 and 200 stops, it cuts route
  length by 14 % and 17.5 %, at roughly 7× and 10× the runtime of NN.

- **The gain is zero at 10 stops.** On small instances, NN often lands on
  a locally optimal tour that 2-opt cannot improve. Local search can only
  exploit improvements reachable by reversing tour segments; the starting
  tour matters.

- **Timings below ~1 ms are not meaningful.** The 10-stop runtimes are
  close to the noise floor of single-shot measurement on a JVM. The 50-
  and 200-stop numbers are the ones to trust.

- **One run per configuration.** These are not medians of many runs.
  Repeated measurements would be needed to report confidence intervals;
  the numbers here reflect a single representative execution after
  warm-up.