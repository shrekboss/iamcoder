package org.coder.concurrency.programming.pattern._13_active_objects._reusable;

import java.util.concurrent.Future;

public interface SampleActiveObject {
    public Future<String> process(String arg, int i);
}