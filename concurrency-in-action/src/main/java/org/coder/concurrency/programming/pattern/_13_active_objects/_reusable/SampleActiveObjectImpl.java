package org.coder.concurrency.programming.pattern._13_active_objects._reusable;

import org.coder.concurrency.programming.util.Debug;

public class SampleActiveObjectImpl {

    public String doProcess(String arg, int i) {
        Debug.info("doProcess start");
        try {
            // 模拟一个比较耗时的操作
            Thread.sleep(500);
        } catch (InterruptedException e) {
            ;
        }
        return arg + "-" + i;
    }

}