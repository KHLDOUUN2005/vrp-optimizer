# VRP Optimizer

*An algorithm-first Vehicle Routing Problem solver — from hand-written TSP heuristics to a full-stack web application with real road data.*

---

## What it does

Given a depot and a list of delivery stops, the system computes optimized routes for a single truck using real road distances. It returns the ordered stops, the total distance, and the percentage saved compared to a naive (as-entered) plan.

It is a web application: a dispatcher logs in, picks stops on a map, clicks **Optimize**, and sees the exact road geometry drawn on the map with a cost and time summary.

## Why this project

Vehicle routing is a real, hard, commercially valuable problem. It is NP-hard, which means no fast method guarantees the perfect answer at scale — so the interesting work is in designing heuristics and local search that find very good solutions quickly. Most portfolio projects stop at CRUD. This one deliberately goes the other way:

- The **algorithmic core is written by hand**: brute force, Nearest Neighbor, and 2-opt.
- The solver is developed **decoupled from the plumbing** — it consumes only a distance matrix, so it can be tested and benchmarked in pure Java, without a database or a browser.
- The result is a **measurable claim**: the heuristics are benchmarked against brute-force optimal solutions.

That combination — real algorithms, clean architecture, quantified results — is the point.

## Tech stack

| Area | Tool |
|---|---|
| Backend | Java 21, Spring Boot, Maven, Spring Security (JWT) |
| Frontend | React, Vite, TypeScript, TailwindCSS, Leaflet |
| Database | PostgreSQL |
| Cache | Redis |
| Routing data | OSRM (self-hosted in Docker), Haversine fallback |
| Map tiles | OpenStreetMap |
| Containers | Docker, Docker Compose |
| Testing | JUnit 5, AssertJ, Mockito |

## Project structure

vrp-optimizer/
├── backend/ Java + Maven. Solvers and Spring Boot REST API.
├── frontend/ React + Vite. Map interface and route display.
├── infra/ OSRM configuration and OpenStreetMap data setup.
├── docs/ Benchmarks and design decisions.
└── docker-compose.yml

## Roadmap

- [x] **Phase 0** — Foundations: repo, environment, Maven skeleton
- [x] **Phase 1** — TSP core: brute force, Nearest Neighbor, 2-opt, tests, benchmarks
- [x] **Phase 2** — Distance data: Haversine provider, OSRM provider, Redis cache
- [x] **Phase 3** — Minimal backend + auth: REST API, register, login, optimize endpoint
- [x] **Phase 4** — Frontend map + route display (Leaflet, directional arrows)
- [x] **Phase 5** — Docker Compose: one command runs the full 5-service stack
- [ ] **Phase 6** — CVRP upgrade: multiple trucks, capacity, Clarke-Wright, Or-opt (Future Work)
- [ ] **Phase 7** — VRPTW + benchmarks: time windows, metaheuristics, OR-Tools comparison (Future Work)

## Design decisions

- **Tour mode is configurable.** The solver accepts `OPEN` (ends at the last stop) or `CLOSED` (returns to the depot). Both are tested against brute force. The default in the UI is closed.
- **Distance providers are interchangeable.** The solver consumes a `double[][]` distance matrix and nothing else. Haversine (straight-line) is the default for development and tests; OSRM (real road distances) swaps in behind the same interface.
- **Algorithms first.** The solver was built and benchmarked before the web plumbing. The core logic has no Spring, no database, and no browser — only Java and JUnit.
- **Stateless Auth.** Register, login, JWT. The API is fully stateless and secured via a custom JWT filter.

## Benchmarks

Random Euclidean instances, Haversine distances, 200 stops.

| Stops | Algorithm | Total distance | Runtime (ms) |
|-------|-----------|----------------|--------------|
| 50    | NN        | 359.79 km      | 0.750        |
| 50    | NN + 2-opt| 310.03 km      | 5.332        |
| 200   | NN        | 671.52 km      | 6.441        |
| 200   | NN + 2-opt| 554.03 km      | 62.547       |

*2-opt reduces total route distance by up to **17.5%** on 200 stops.*

## How to run it

The entire stack is containerized. You only need Docker installed.

```bash
git clone https://github.com/KHLDOUUN2005/vrp-optimizer.git
cd vrp-optimizer
docker compose up --build -d

Then open http://localhost in your browser.

Author
Khalid El-Harmoud — github.com/KHLDOUUN2005