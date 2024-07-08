package org.coder.concurrency.programming.pattern._13_active_objects.mmsc;

import java.io.Closeable;

public interface RequestPersistence extends Closeable {

    void store(MMSDeliverRequest request);
}