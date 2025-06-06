package org.coder.err.programming._2_design_chapter.productionready.info;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Component
// 对外暴露程序内部重要组件的状态数据
public class ThreadPoolInfoContributor implements InfoContributor {
    private static Map threadPoolInfo(ThreadPoolExecutor threadPool) {
        Map<String, Object> info = new HashMap<>();
        info.put("poolSize", threadPool.getPoolSize());
        info.put("corePoolSize", threadPool.getCorePoolSize());
        info.put("largestPoolSize", threadPool.getLargestPoolSize());
        info.put("maximumPoolSize", threadPool.getMaximumPoolSize());
        info.put("completedTaskCount", threadPool.getCompletedTaskCount());
        return info;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("demoThreadPool", threadPoolInfo(ThreadPoolProvider.getDemoThreadPool()));
        builder.withDetail("ioThreadPool", threadPoolInfo(ThreadPoolProvider.getIOThreadPool()));
    }
}
