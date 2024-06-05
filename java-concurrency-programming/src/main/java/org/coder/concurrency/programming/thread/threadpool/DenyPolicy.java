package org.coder.concurrency.programming.thread.threadpool;

/**
 * 4. 主要用于当 Queue 中的 runnable 达到了 limit 上限时，决定采取何种策略通知提交者
 * 该接口定义了三种默认的实现
 * a. DiscardDenyPolicy
 * b. AbortDenyPolicy
 * c. RunnerDenyPolicy
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface DenyPolicy {

    void reject(Runnable runnable, ThreadPool threadPool);


    class DiscardDenyPolicy implements DenyPolicy {

        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {
            //do nothing
        }
    }

    class AbortDenyPolicy implements DenyPolicy {

        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {
            throw new RunnableDenyException("The runnable " + runnable + " will be abort.");
        }
    }

    class RunnerDenyPolicy implements DenyPolicy {

        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {
            if (!threadPool.isShutdown()) {
                runnable.run();
            }
        }
    }
}
