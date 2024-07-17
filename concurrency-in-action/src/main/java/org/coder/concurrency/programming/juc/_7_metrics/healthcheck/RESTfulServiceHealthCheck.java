package org.coder.concurrency.programming.juc._7_metrics.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 2.自定义Health Check
 * 虽然引入了Health Check插件包的依赖，但事实上，该包中并未提供多少Health Check的相关实现，如果想要对自己的应用程序做相关Health Check也是非常容易的，
 * 比如，如果想要查看RESTful接口是否能够正常提供服务，那么通过扩展Health Check就可以轻而易举地做到，示例代码如下。
 * <p>
 * 继承HealthCheck实现check方法，该方法利用OkHttpClient对某个RESTful API进行了调用，如果HTTP状态码为200，我们就认为该服务正常（healthy），否则将会返回unhealthy。
 */
public class RESTfulServiceHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:10002/alexwang/ping")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return Result.healthy("The REATful API service work well.");
            }
        } catch (Exception e) {

        }
        return Result.unhealthy("Detected RESTful server is unhealthy.");
    }

}