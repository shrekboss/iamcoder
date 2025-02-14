package org.coder.err.programming._1_code_chapter.connPool.twotimeoutconfig;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.io.IOException;

@RequestMapping("twotimeoutconfig")
@Slf4j
@RestController
public class TwoTimeoutConfigController {

    private static CloseableHttpClient httpClient = null;

    static {
        httpClient = HttpClients.createSystem();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                httpClient.close();
            } catch (IOException ignored) {
            }
        }));
    }

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * curl http://localhost:45678/twotimeoutconfig/mysql
     */
    @GetMapping("mysql")
    public String mysql() {
        //调试StandardSocketFactory进行验证
        return jdbcTemplate.queryForObject("SELECT 'OK'", String.class);
    }

    /**
     * curl http://localhost:45678/twotimeoutconfig/redis
     */
    @GetMapping("redis")
    public String redis() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1);
        // 请求连接超时
        config.setMaxWaitMillis(10000);
        // 连接超时
        try (JedisPool jedisPool = new JedisPool(config, "127.0.0.1", 60000, 5000);
             Jedis jedis = jedisPool.getResource()) {
            return jedis.set("test", "test");
        }
    }

    /**
     * curl http://localhost:45678/twotimeoutconfig/http
     */
    @GetMapping("http")
    public String http() {
        RequestConfig requestConfig = RequestConfig.custom()
                // 连接超时
                .setConnectTimeout(5000)
                // 请求连接超时
                .setConnectionRequestTimeout(10000)
                .build();
        HttpGet httpGet = new HttpGet("http://127.0.0.1:45678/twotimeoutconfig/test");
        httpGet.setConfig(requestConfig);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}
