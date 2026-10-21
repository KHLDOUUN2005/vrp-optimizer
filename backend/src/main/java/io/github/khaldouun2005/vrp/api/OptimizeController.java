package io.github.khaldouun2005.vrp.api;

import java.net.http.HttpClient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.khaldouun2005.vrp.api.dto.OptimizeRequest;
import io.github.khaldouun2005.vrp.api.dto.OptimizeResponse;
import io.github.khaldouun2005.vrp.api.dto.StopDto;
import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Route;
import io.github.khaldouun2005.vrp.model.Stop;
import io.github.khaldouun2005.vrp.model.TourMode;
import io.github.khaldouun2005.vrp.provider.DistanceProvider;
import io.github.khaldouun2005.vrp.provider.HaversineDistanceProvider;
import io.github.khaldouun2005.vrp.solver.BruteForceOptimizer;
import io.github.khaldouun2005.vrp.solver.NearestNeighborOptimizer;
import io.github.khaldouun2005.vrp.solver.RouteOptimizer;
import io.github.khaldouun2005.vrp.solver.TwoOptOptimizer;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class OptimizeController {
	
	private final HaversineDistanceProvider haverProvider;
	private final DistanceProvider distanceProvider;
	private final BruteForceOptimizer brutFroce_OPT;
	private final NearestNeighborOptimizer nearestN_OPT;
	private final TwoOptOptimizer two_OPT;
	
	public OptimizeController(HaversineDistanceProvider haverProvider,
			@Qualifier("osrmProvider") DistanceProvider distanceProvider,
			BruteForceOptimizer brutFroce_OPT,
			NearestNeighborOptimizer nearestN_OPT,
			TwoOptOptimizer two_OPT) {
		
		this.haverProvider = haverProvider;
		this.distanceProvider = distanceProvider;
		this.brutFroce_OPT = brutFroce_OPT;
		this.nearestN_OPT = nearestN_OPT;
		this.two_OPT = two_OPT;
	}
	
	@PostMapping("optimize")
	public OptimizeResponse optimize(@Valid @RequestBody OptimizeRequest optReq) {
		long startTime = System.nanoTime();
		DistanceProvider provider = getPro(optReq.provider());
		DistanceMatrix disM = provider.computeMatrix(convertToStops(optReq.stops()));
		RouteOptimizer optimizer = getOptimizer(optReq.optimizer());
		return createResponse(optimizer, disM, optReq, startTime);
	}


	private OptimizeResponse createResponse(RouteOptimizer optimizer, DistanceMatrix disM, OptimizeRequest optReq, long startTime) {
		Route route;
		try {
		    route = optimizer.solve(disM, optReq.depotIndex(), optReq.mode());
		} catch (IllegalArgumentException e) {
		    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		List<StopDto> stopsDto = convertToStopsDTO(route.order(), optReq.stops());
		double optDist = route.totalDistance();
		double naiveDist = getNaiveDistance(optReq, disM);
		double savingPrct = naiveDist==0?0.0:((naiveDist-optDist)/naiveDist)*100;
		String providerUsed = optReq.provider() == null ? "OSRM_Provider" : optReq.provider();
		String optimizerUsed = optReq.optimizer() == null ? "TWO_OPT" : optReq.optimizer();
		long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;
		List<double[]> geometry = getRouteGeometry(stopsDto, providerUsed, optReq.mode());
		
		return new OptimizeResponse(
				stopsDto,
				geometry,
				optDist,
				naiveDist,
				savingPrct,
				providerUsed,
				optimizerUsed,
				optReq.mode(),
				elapsedMs);
	}

	private double getNaiveDistance(OptimizeRequest optReq, DistanceMatrix disM) {
		List<StopDto> stops = optReq.stops();
		double distance = 0.0;
		int before = optReq.depotIndex();
		for (int i=0; i<stops.size(); i++) {
			if (i == optReq.depotIndex()) {
				continue;
			}
			distance += disM.cost(before, i);
			before = i;
		}
		if (optReq.mode() == TourMode.CLOSED) distance += disM.cost(before, optReq.depotIndex());
		return distance;
	}

	private List<StopDto> convertToStopsDTO(List<Integer> optimized, List<StopDto> stpsDto) {
		List<StopDto> stps = new ArrayList<>();
		for (Integer i : optimized) {
			for (StopDto stp : stpsDto)
				if (stp.id() == i) {
					stps.add(new StopDto(stp.id(), stp.label(), stp.latitude(), stp.longitude()));
					break;
				}
		}
		return stps;
	}

	private RouteOptimizer getOptimizer(String optimizer) {
	    if (optimizer == null || optimizer.equals("two-opt")) {
	        return two_OPT;
	    } else if (optimizer.equals("nearest-neighbor")) {
	        return nearestN_OPT;
	    } else if (optimizer.equals("brute-force")) {
	        return brutFroce_OPT;
	    } else {
	        return two_OPT; 
	    }
	}

	private List<Stop> convertToStops(List<StopDto> stopsDTO) {
		List<Stop> stops = new ArrayList<>();
		for (StopDto stpD : stopsDTO) {
			stops.add(new Stop(stpD.id(), stpD.label(), stpD.latitude(), stpD.longitude()));
		}
		return stops;
	}

	private DistanceProvider getPro(String pro) {
	    if (pro == null || pro.equals("osrm")) {
	        return distanceProvider;
	    } else {
	        return haverProvider;
	    }
	}
	private List<double[]> getRouteGeometry(List<StopDto> orderedStops, String provider, TourMode mode) {
	    List<double[]> geometry = new ArrayList<>();
	    
	    List<StopDto> routePoints = new ArrayList<>(orderedStops);
	    
	    if (mode == TourMode.CLOSED && !routePoints.isEmpty()) {
	        routePoints.add(routePoints.get(0)); 
	    }

	    if ("haversine".equalsIgnoreCase(provider)) {
	        for (StopDto stop : routePoints) {
	            geometry.add(new double[]{stop.latitude(), stop.longitude()});
	        }
	        return geometry;
	    }

	    StringBuilder coords = new StringBuilder();
	    for (int i = 0; i < routePoints.size(); i++) {
	        StopDto stop = routePoints.get(i);
	        coords.append(stop.longitude()).append(",").append(stop.latitude());
	        if (i < routePoints.size() - 1) coords.append(";");
	    }

	    String url = "http://localhost:5000/route/v1/driving/" + coords.toString() + "?geometries=geojson&overview=full";

	    try {
	        HttpClient client = HttpClient.newHttpClient();
	        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
	        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

	        if (response.statusCode() == 200) {
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode root = mapper.readTree(response.body());
	            JsonNode coordsArray = root.get("routes").get(0).get("geometry").get("coordinates");

	            for (JsonNode pair : coordsArray) {
	                double lng = pair.get(0).asDouble();
	                double lat = pair.get(1).asDouble();
	                geometry.add(new double[]{lat, lng});
	            }
	            return geometry;
	        }
	    } catch (Exception e) {
	        System.err.println("OSRM geometry failed: " + e.getMessage());
	    }

	    for (StopDto stop : routePoints) {
	        geometry.add(new double[]{stop.latitude(), stop.longitude()});
	    }
	    return geometry;
	}
}
