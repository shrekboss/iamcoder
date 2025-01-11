package org.coder.concurrency.programming.pattern._12_worker_thread.ftp;

import org.coder.concurrency.programming.pattern._5_future.ftp.FTPUploader;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class FileTransferTask implements Callable<File> {
    public final Future<FTPUploader> ftpUtilHodler;
    public final File file2Transfer;

    public FileTransferTask(Future<FTPUploader> ftpUtilHolder,
                            File file2Transfer) {
        this.ftpUtilHodler = ftpUtilHolder;
        this.file2Transfer = file2Transfer;
    }

    @Override
    public File call() throws Exception {
        File transferedFile;
        ftpUtilHodler.get().upload(file2Transfer);
        transferedFile = file2Transfer;
        return transferedFile;
    }
}