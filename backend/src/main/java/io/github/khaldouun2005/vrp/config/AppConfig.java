package io.github.khaldouun2005.vrp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.khaldouun2005.vrp.provider.*;
import io.github.khaldouun2005.vrp.solver.*;
import redis.clients.jedis.Jedis;

@Configuration
public class AppConfig {

    @Bean
    public Jedis jedis(@Value("${app.redis.host}") String redisHost, 
                       @Value("${app.redis.port}") int redisPort) {
        return new Jedis(redisHost, redisPort);
    }

    @Bean
    public HaversineDistanceProvider haversineProvider() {
        return new HaversineDistanceProvider();
    }

    @Bean
    public DistanceProvider osrmProvider(Jedis jedis, 
                                         @Value("${app.osrm-url}") String osrmUrl,
                                         @Value("${app.cache.ttl-seconds}") int ttlSeconds) {
        DistanceProvider osrm = new OsrmDistanceProvider(osrmUrl);
        return new CachingDistanceProvider(osrm, jedis, ttlSeconds);
    }

    @Bean
    public BruteForceOptimizer bruteForceOptimizer() {
        return new BruteForceOptimizer();
    }

    @Bean
    public NearestNeighborOptimizer nearestNeighborOptimizer() {
        return new NearestNeighborOptimizer();
    }

    @Bean
    public TwoOptOptimizer twoOptOptimizer() {
        return new TwoOptOptimizer();
    }
}