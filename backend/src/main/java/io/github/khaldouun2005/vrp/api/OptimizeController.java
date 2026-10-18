package io.github.khaldouun2005.vrp.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
		return new OptimizeResponse(
				stopsDto,
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
}
