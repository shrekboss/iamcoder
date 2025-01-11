package org.coder.err.programming._2_design_chapter.nosqluse.redisvsmysql;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@Slf4j
@RequestMapping("redisvsmysql")
public class PerformanceController {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("redis")
    public void redis() {
        Assert.assertTrue(stringRedisTemplate.opsForValue()
                .get("item" + (ThreadLocalRandom.current().nextInt(CommonMistakesApplication.ROWS) + 1))
                .equals(CommonMistakesApplication.PAYLOAD));
    }

    @GetMapping("redis2")
    public void redis2() {
        Assert.assertTrue(stringRedisTemplate.keys("item71*").size() == 1111);
    }

    @GetMapping("mysql")
    public void mysql() {
        Assert.assertTrue(jdbcTemplate.queryForObject("SELECT data FROM `r` WHERE name=?",
                        new Object[]{("item" + (ThreadLocalRandom.current().nextInt(CommonMistakesApplication.ROWS) + 1))}, String.class)
                .equals(CommonMistakesApplication.PAYLOAD));
    }

    @GetMapping("mysql2")
    public void mysql2() {
        Assert.assertTrue(jdbcTemplate.queryForList("SELECT name FROM `r` WHERE name LIKE 'item71%'", String.class).size() == 1111);
    }
}
