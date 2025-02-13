package org.coder.concurrency.programming.pattern._12_worker_thread.ftp;

public class RecordSaveTask {
    public final RecordDefinition[] recordDefinitions;
    public final int targetFileIndex;
    public final String recordDay;

    public RecordSaveTask(RecordDefinition[] recordDefinitions, int targetFileIndex) {
        this.recordDefinitions = recordDefinitions;
        this.targetFileIndex = targetFileIndex;
        this.recordDay = null;
    }

    public RecordSaveTask(String recordDay, int targetFileIndex) {
        this.recordDefinitions = null;
        this.targetFileIndex = targetFileIndex;
        this.recordDay = recordDay;
    }
}