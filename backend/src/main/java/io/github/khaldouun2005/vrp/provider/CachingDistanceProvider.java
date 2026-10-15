package io.github.khaldouun2005.vrp.provider;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Stop;
import redis.clients.jedis.Jedis;

public class CachingDistanceProvider implements DistanceProvider{
	private final DistanceProvider delegate;
    private final Jedis jedis;
    private final int ttlSeconds;
    
    public CachingDistanceProvider(DistanceProvider delegate, Jedis jedis, int ttlSeconds) {
    	this.delegate = delegate;
        this.jedis = jedis;
        this.ttlSeconds = ttlSeconds;
    }
    
	@Override
	public DistanceMatrix computeMatrix(List<Stop> stops) {
		StringBuilder str = new StringBuilder();
		for (Stop stop : stops) {
			str.append(String.format("%.6f",stop.latitude()));
			str.append(",");
			str.append(String.format("%.6f",stop.longitude()));
			str.append(";");
		}
		String ls = str.toString();
		DistanceMatrix distM;
		double[][] matrix;
		String value = jedis.get(ls);
		ObjectMapper mapper= new ObjectMapper(); 
		if (value == null) {
			distM  = delegate.computeMatrix(stops);
			matrix = new double[distM.size()][distM.size()];
			for (int i=0; i<distM.size(); i++) {
				matrix[i][i] = distM.cost(i, i);
				for (int j=i+1; j<distM.size(); j++) {
					matrix[i][j] = distM.cost(i, j);
					matrix[j][i] = distM.cost(j, i);
				}
			}
			try {
				value = mapper.writeValueAsString(matrix);
				jedis.setex(ls, ttlSeconds, value);
			} catch (JsonProcessingException e) {
			}
		}
		else {
			try {
				matrix = mapper.readValue(value, double[][].class);
				distM = DistanceMatrix.fromGrid(matrix);
			} catch (JsonProcessingException e) {
				distM = delegate.computeMatrix(stops);
				jedis.del(ls);
				e.printStackTrace();
			}
		}
		return distM;
	}

}
