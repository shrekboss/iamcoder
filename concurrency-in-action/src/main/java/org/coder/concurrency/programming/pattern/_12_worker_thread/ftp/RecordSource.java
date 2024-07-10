package org.coder.concurrency.programming.pattern._12_worker_thread.ftp;

import java.io.IOException;

public interface RecordSource {

    void close() throws IOException;

    boolean hasNext();

    Record next();

}