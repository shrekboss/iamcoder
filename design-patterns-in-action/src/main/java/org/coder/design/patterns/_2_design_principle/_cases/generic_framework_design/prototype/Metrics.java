package org.coder.design.patterns._2_design_principle._cases.generic_framework_design.prototype;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Metrics {
    // Map的key是接口名称，value对应接口请求的响应时间或时间戳；
    private Map<String, List<Double>> responseTimes = new HashMap<>();
    private Map<String, List<Double>> timestamps = new HashMap<>();
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public void recordResponseTime(String apiName, double responseTime) {
        responseTimes.putIfAbsent(apiName, new ArrayList<>());
        responseTimes.get(apiName).add(responseTime);
    }

    public void recordTimestamp(String apiName, double timestamp) {
        timestamps.putIfAbsent(apiName, new ArrayList<>());
        timestamps.get(apiName).add(timestamp);
    }

    public void startRepeatedReport(long period, TimeUnit unit) {
        executor.scheduleAtFixedRate(() -> {
            Gson gson = new Gson();
            Map<String, Map<String, Double>> stats = new HashMap<>();
            for (Map.Entry<String, List<Double>> entry : responseTimes.entrySet()) {
                String apiName = entry.getKey();
                List<Double> apiRespTimes = entry.getValue();
                stats.putIfAbsent(apiName, new HashMap<>());
                stats.get(apiName).put("max", max(apiRespTimes));
                stats.get(apiName).put("avg", avg(apiRespTimes));
            }

            for (Map.Entry<String, List<Double>> entry : timestamps.entrySet()) {
                String apiName = entry.getKey();
                List<Double> apiTimestamps = entry.getValue();
                stats.putIfAbsent(apiName, new HashMap<>());
                stats.get(apiName).put("count", (double) apiTimestamps.size());
            }
            System.out.println(gson.toJson(stats));
        }, 0, period, unit);
    }

    private double max(List<Double> dataset) {
        //省略代码实现
        return 0d;
    }

    private double avg(List<Double> dataset) {
        //省略代码实现
        return 0d;
    }
}