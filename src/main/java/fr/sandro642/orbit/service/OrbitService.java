package fr.sandro642.orbit.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sandro642.orbit.app.core.Candle;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class OrbitService {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final IndicatorEngine engine = new IndicatorEngine();

    public Mono<List<Candle>> fetchCandles(String coinId, String days) {
        return Mono.fromCallable(() -> {
            String url = "https://api.coingecko.com/api/v3/coins/" + coinId + "/ohlc?vs_currency=usd&days=" + days;
            Request req = new Request.Builder().url(url).build();
            try (Response res = client.newCall(req).execute()) {
                List<List<Object>> raw = mapper.readValue(res.body().string(), new TypeReference<>() {});
                List<Candle> list = new ArrayList<>();
                for (List<Object> r : raw) {
                    list.add(new Candle(((Number) r.get(0)).longValue(), ((Number) r.get(1)).doubleValue(),
                            ((Number) r.get(2)).doubleValue(), ((Number) r.get(3)).doubleValue(), ((Number) r.get(4)).doubleValue()));
                }
                engine.calculateAll(list);
                return list;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
