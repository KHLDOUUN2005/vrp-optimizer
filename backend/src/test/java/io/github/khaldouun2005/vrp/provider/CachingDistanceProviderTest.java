package io.github.khaldouun2005.vrp.provider;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.khaldouun2005.vrp.model.DistanceMatrix;
import io.github.khaldouun2005.vrp.model.Stop;
import redis.clients.jedis.Jedis;

public class CachingDistanceProviderTest {

    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;
    private static final int TTL_SECONDS = 3600;

    private static final List<Stop> STOPS = List.of(
        new Stop(0, "A", 33.5731, -7.5898),
        new Stop(1, "B", 33.6000, -7.6000),
        new Stop(2, "C", 33.5797, -7.5503)
    );

    private Jedis jedis;
    private CountingProvider counting;
    private CachingDistanceProvider provider;

    @BeforeEach
    void setUp() {
        assumeTrue(redisIsReachable(), "Redis not running on localhost:6379 — skipping");
        jedis = new Jedis(REDIS_HOST, REDIS_PORT);
        jedis.flushDB();
        counting = new CountingProvider();
        provider = new CachingDistanceProvider(counting, jedis, TTL_SECONDS);
    }

    @AfterEach
    void tearDown() {
        if (jedis != null) jedis.close();
    }

    @Test
    void returnsCorrectMatrixOnCacheMiss() {
        DistanceMatrix m = provider.computeMatrix(STOPS);
        assertThat(m.size()).isEqualTo(3);
        assertThat(counting.callCount).isEqualTo(1);
    }

    @Test
    void secondCallUsesCache() {
        provider.computeMatrix(STOPS);
        provider.computeMatrix(STOPS);
        assertThat(counting.callCount).isEqualTo(1);
    }

    @Test
    void differentStopsMissTheCache() {
        List<Stop> other = List.of(
            new Stop(0, "X", 33.5000, -7.5000),
            new Stop(1, "Y", 33.5500, -7.5500)
        );
        provider.computeMatrix(STOPS);
        provider.computeMatrix(other);
        assertThat(counting.callCount).isEqualTo(2);
    }

    @Test
    void sameStopsDifferentOrderMissTheCache() {
        List<Stop> reversed = List.of(STOPS.get(2), STOPS.get(1), STOPS.get(0));
        provider.computeMatrix(STOPS);
        provider.computeMatrix(reversed);
        assertThat(counting.callCount).isEqualTo(2);
    }

    @Test
    void sameCoordinatesProduceCacheHitEvenWithDifferentStopObjects() {
        List<Stop> equivalent = List.of(
            new Stop(0, "Other label", 33.5731, -7.5898),
            new Stop(1, "Other label", 33.6000, -7.6000),
            new Stop(2, "Other label", 33.5797, -7.5503)
        );
        provider.computeMatrix(STOPS);
        provider.computeMatrix(equivalent);
        assertThat(counting.callCount).isEqualTo(1);
    }

    @Test
    void cachedMatrixMatchesFreshMatrix() {
        DistanceMatrix first  = provider.computeMatrix(STOPS);
        DistanceMatrix second = provider.computeMatrix(STOPS);
        assertThat(second.size()).isEqualTo(first.size());
        for (int i = 0; i < first.size(); i++) {
            for (int j = 0; j < first.size(); j++) {
                assertThat(second.cost(i, j)).isEqualTo(first.cost(i, j));
            }
        }
    }

    private static boolean redisIsReachable() {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(REDIS_HOST, REDIS_PORT), 500);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static class CountingProvider implements DistanceProvider {
        int callCount = 0;
        private final DistanceProvider inner = new HaversineDistanceProvider();

        @Override
        public DistanceMatrix computeMatrix(List<Stop> stops) {
            callCount++;
            return inner.computeMatrix(stops);
        }
    }
}