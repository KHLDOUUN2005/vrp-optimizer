# OSRM setup

Local OSRM routing engine for real road distances.

## Data

- Extract source: BBBike (https://extract.bbbike.org/)
- Region: Casablanca, ~30 km bounding box
- Format: OSM PBF
- File: `data/casablanca.osm.pbf` (not committed — regenerate via BBBike)

## Preprocessing

Run once from this directory:

    docker run -t -v "%cd%/data:/data" osrm/osrm-backend osrm-extract -p /opt/car.lua /data/casablanca.osm.pbf
    docker run -t -v "%cd%/data:/data" osrm/osrm-backend osrm-partition /data/casablanca.osrm
    docker run -t -v "%cd%/data:/data" osrm/osrm-backend osrm-customize /data/casablanca.osrm

## Running the server

    docker run -t -i -p 5000:5000 -v "%cd%/data:/data" osrm/osrm-backend osrm-routed --algorithm mld /data/casablanca.osrm

Server listens on http://localhost:5000.

## Verify

    curl "http://localhost:5000/table/v1/driving/-7.6,33.6;-7.5,33.55?annotations=distance"