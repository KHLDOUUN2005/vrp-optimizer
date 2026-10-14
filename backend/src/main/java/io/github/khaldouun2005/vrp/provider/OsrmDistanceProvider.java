package io.github.khaldouun2005.vrp.provider;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Stop;

public class OsrmDistanceProvider implements DistanceProvider{
	private final String baseUrl;
	
	public OsrmDistanceProvider(String baseUrl) {
	    if (baseUrl == null) throw new IllegalArgumentException("baseUrl must not be null");
	    this.baseUrl = baseUrl;
	}
	
	@Override
	public DistanceMatrix computeMatrix(List<Stop> stops) {
		if (stops == null) throw new IllegalArgumentException("stops must not be null");
		if (stops.size() < 2) throw new IllegalArgumentException("need at least 2 stops, got " + stops.size());
		HttpClient client = HttpClient.newHttpClient();
		double[][] distances;
		StringBuilder url = new StringBuilder(baseUrl).append("/table/v1/driving/");
		String json;
		HttpRequest req;
		HttpResponse<String> response;
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsn;
		for (int i = 0; i < stops.size(); i++) {
		    Stop stop = stops.get(i);
		    url.append(String.format("%.6f,%.6f", stop.longitude(), stop.latitude()));
		    if (i < stops.size() - 1) url.append(";");
		}
		url.append("?annotations=distance");
		String finalURL = url.toString();
		req = HttpRequest.newBuilder(URI.create(finalURL)).GET().build();
		try {
			response = client.send(req, BodyHandlers.ofString());
			json = response.body();
			if (response.statusCode() != 200) {
			    throw new RuntimeException("OSRM returned HTTP " + response.statusCode() + ": " + json);
			}
			jsn = mapper.readTree(json);
			JsonNode codeNode = jsn.get("code");
			if (codeNode == null || !"Ok".equals(codeNode.asText())) {
			    throw new RuntimeException("OSRM error: " + (codeNode == null ? "missing code" : codeNode.asText()));
			}
			distances = mapper.convertValue(jsn.get("distances"), double[][].class);
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		    throw new RuntimeException("OSRM request interrupted", e);
		} catch (IOException e) {
		    throw new RuntimeException("OSRM request failed: " + e.getMessage(), e);
		}
		for (int i=0; i<distances.length; i++) {
			distances[i][i] = distances[i][i]/1000.0;
			for (int j=i+1; j<distances.length; j++) {
				distances[i][j] = distances[i][j]/1000.0;
				distances[j][i] = distances[j][i]/1000.0;
			}
		}
		return DistanceMatrix.fromGrid(distances);
	}
}
