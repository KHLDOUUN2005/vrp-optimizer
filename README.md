# VRP Optimizer

*An algorithm-first Vehicle Routing Problem solver — from hand-written TSP heuristics to a full Capacitated VRP with real road data.*

---

## What it does

Given a depot and a list of delivery stops, the system computes optimized routes for one or more trucks, respecting capacity constraints, using real road distances. It returns the ordered stops per route, the total distance and time, and the percentage saved compared to a naive (as-entered) plan.

The final product is a web application: a dispatcher picks stops on a map, clicks **Optimize**, and sees the result drawn on the map with a cost summary.

## Why this project

Vehicle routing is a real, hard, commercially valuable problem. It is NP-hard, which means no fast method guarantees the perfect answer at scale — so the interesting work is in designing heuristics and local search that find very good solutions quickly. Most portfolio projects stop at CRUD. This one deliberately goes the other way:

- The **algorithmic core is written by hand**: brute force, Nearest Neighbor, 2-opt, Clarke-Wright Savings, Or-opt.
- The solver is developed **decoupled from the plumbing** — it consumes only a distance matrix, so it can be tested and benchmarked in pure Java, without a database or a browser.
- The result is a **measurable claim**: how close the solver comes to a professional baseline (Google OR-Tools) on standard benchmark instances.

That combination — real algorithms, clean architecture, quantified results — is the point.

## Tech stack

| Area | Tool |
|---|---|
| Language | Java 21 (backend), TypeScript (frontend) |
| Build | Maven |
| Backend | Spring Boot |
| Frontend | React + Vite + TailwindCSS + Leaflet |
| Database | PostgreSQL |
| Cache | Redis |
| Routing data | OSRM (self-hosted), Haversine fallback |
| Map tiles | OpenStreetMap |
| Containers | Docker, Docker Compose |
| Testing | JUnit 5, AssertJ, Mockito |
| Benchmarking | Google OR-Tools (comparison baseline) |

## Project structure

```
vrp-optimizer/
├── backend/    Java + Maven. The solver and the Spring Boot API.
├── frontend/   React + Vite. Map interface and route display.
├── infra/      Docker, Compose, and deployment configuration.
└── docs/       Benchmarks, design decisions, and notes.
```

## Roadmap

- [ ] **Phase 0** — Foundations: repo, environment, Maven skeleton
- [ ] **Phase 1** — TSP core: brute force, Nearest Neighbor, 2-opt, tests, benchmarks
- [ ] **Phase 2** — Distance data: Haversine provider, OSRM provider, Redis cache
- [ ] **Phase 3** — Minimal backend + auth: REST API, register, login, optimize endpoint
- [ ] **Phase 4** — Frontend map + route display
- [ ] **Phase 5** — Docker Compose: one command runs the full stack
- [ ] **Phase 6** — CVRP upgrade: multiple trucks, capacity, Clarke-Wright, Or-opt
- [ ] **Phase 7** — VRPTW + benchmarks: time windows, metaheuristics, OR-Tools comparison

## Design decisions

- **Tour mode is configurable.** The solver accepts `OPEN` (ends at the last stop) or `CLOSED` (returns to the depot). Both are tested against brute force. The default in the UI is closed.
- **Distance providers are interchangeable.** The solver consumes a `double[][]` distance matrix and nothing else. Haversine (straight-line) is the default for development and tests; OSRM (real road distances) swaps in behind the same interface.
- **Algorithms first.** The solver is built and benchmarked before the web plumbing. Phase 1 has no Spring, no database, no browser — only Java and JUnit.
- **Minimal auth.** Register, login, JWT. No email verification, no password reset, no refresh tokens. It is a supporting feature, not a differentiator.

## Benchmarks

Random Euclidean instances, Haversine distances. Populated as algorithms are implemented.

| Stops | Algorithm | Total distance | Runtime (ms) |
|-------|-----------|----------------|--------------|
| 10    | —         | —              | —            |
| 50    | —         | —              | —            |
| 200   | —         | —              | —            |

## Author

Khalid El-Harmoud — [github.com/KHLDOUUN2005](https://github.com/KHLDOUUN2005)