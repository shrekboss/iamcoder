package org.coder.concurrency.programming.pattern._12_worker_thread.ftp;

public class RecordSaveTask {
    public final Record[] records;
    public final int targetFileIndex;
    public final String recordDay;

    public RecordSaveTask(Record[] records, int targetFileIndex) {
        this.records = records;
        this.targetFileIndex = targetFileIndex;
        this.recordDay = null;
    }

    public RecordSaveTask(String recordDay, int targetFileIndex) {
        this.records = null;
        this.targetFileIndex = targetFileIndex;
        this.recordDay = recordDay;
    }
}