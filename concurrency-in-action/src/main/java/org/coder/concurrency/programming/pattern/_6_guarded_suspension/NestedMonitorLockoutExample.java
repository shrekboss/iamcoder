package org.coder.concurrency.programming.pattern._6_guarded_suspension;

import org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm.Blocker;
import org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm.ConditionVarBlocker;
import org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm.GuardedAction;
import org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm.Predicate;
import org.coder.concurrency.programming.util.Debug;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 * 本程序是为了演示“嵌套监视器锁死“而写的，因此本程序需要通过手工终止进程才能结束。
 */
public class NestedMonitorLockoutExample {

    public static void main(String[] args) {
        final Helper helper = new Helper();
        Debug.info("Before calling guardedMethod.");

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                String result;
                result = helper.xGuardedMethod("test");
                Debug.info(result);
            }

        });
        t.start();

        final Timer timer = new Timer();

        // 延迟50ms调用helper.stateChanged方法
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                helper.xStateChanged();
                timer.cancel();
            }

        }, 50, 10);

    }

    private static class Helper {
        private volatile boolean isStateOK = false;
        private final Predicate stateBeOK = new Predicate() {

            @Override
            public boolean evaluate() {
                return isStateOK;
            }

        };

        private final Blocker blocker = new ConditionVarBlocker();

        public synchronized String xGuardedMethod(final String message) {
            GuardedAction<String> ga = new GuardedAction<String>(stateBeOK) {

                @Override
                public String call() throws Exception {
                    return message + "->received.";
                }

            };
            String result = null;
            try {
                result = blocker.callWithGuard(ga);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        public synchronized void xStateChanged() {
            try {
                blocker.signalAfter(new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        isStateOK = true;
                        Debug.info("state ok.");
                        return Boolean.TRUE;
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}