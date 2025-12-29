package org.coder.concurrency.programming.pattern._7_context;

import org.coder.concurrency.programming.pattern._7_context.simulate.Configuration;
import org.coder.concurrency.programming.pattern._7_context.simulate.OtherResource;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ActionContext {

    private static final ThreadLocal<Configuration> configuration = ThreadLocal.withInitial(Configuration::new);

    private static final ThreadLocal<OtherResource> otherResource = ThreadLocal.withInitial(OtherResource::new);

    public static void setConfiguration(Configuration conf) {
        configuration.set(conf);
    }

    public static Configuration getConfiguraiton() {
        return configuration.get();
    }

    public static void setOtherResource(OtherResource oResource) {
        otherResource.set(oResource);
    }

    public static OtherResource getOtherResource() {
        return otherResource.get();
    }
}
