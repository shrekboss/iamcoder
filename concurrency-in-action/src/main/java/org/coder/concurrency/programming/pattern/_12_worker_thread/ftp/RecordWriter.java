package org.coder.concurrency.programming.pattern._12_worker_thread.ftp;

import org.coder.concurrency.programming.util.Debug;
import org.coder.concurrency.programming.util.Tools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class RecordWriter {

    private static final RecordWriter INSTANCE = new RecordWriter();

    // HashMap不是线程安全的，但RecordWriter实例是在单线程中使用的，因此不会产生问题。
    private static Map<String, PrintWriter> printWriterMap = new HashMap<String, PrintWriter>();

    private static String baseDir;
    private static final char FIELD_SEPARATOR = '|';

    // SimpleDateFormat不是线程安全的，但RecordWriter实例是在单线程中使用的，因此不会产生问题。
    private static final SimpleDateFormat DIRECTORY_NAME_FORMATTER = new SimpleDateFormat(
            "yyMMdd");
    private static final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd HH:MM:ss");

    private static final DecimalFormat FILE_INDEX_FORMATTER = new DecimalFormat(
            "0000");

    private static final int RECORD_JOIN_SIZE = Config.RECORD_JOIN_SIZE;

    private static final FieldPosition FIELD_POS = new FieldPosition(
            DateFormat.Field.DAY_OF_MONTH);

    // 私有构造器
    private RecordWriter() {
        baseDir = Tools.getWorkingDir("ch13");
        Debug.info("baseDir %s", baseDir);

    }

    public static RecordWriter getInstance() {
        return INSTANCE;
    }

    public File write(RecordDefinition[] recordDefinitions, int targetFileIndex)
            throws IOException {
        if (null == recordDefinitions || 0 == recordDefinitions.length) {
            throw new IllegalArgumentException("records is null or empty");
        }

        int recordCount = recordDefinitions.length;

        String recordDay;

        recordDay = DIRECTORY_NAME_FORMATTER
                .format(recordDefinitions[0].getOperationTime());

        String fileKey = recordDay + '-' + targetFileIndex;

        PrintWriter pwr = printWriterMap.get(fileKey);

        if (null == pwr) {
            File file = new File(baseDir + '/' + recordDay + "/subspsync-gw-"
                    + FILE_INDEX_FORMATTER.format(targetFileIndex) + ".dat");
            File dir = file.getParentFile();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("No such directory:" + dir);
            }

            pwr = new PrintWriter(new BufferedWriter(new FileWriter(file, true),
                    Config.WRITER_BUFFER_SIZE));

            printWriterMap.put(fileKey, pwr);
        }
        StringBuffer strBuf = new StringBuffer(40);
        int i = 0;
        for (RecordDefinition recordDefinition : recordDefinitions) {
            i++;

            pwr.print(String.valueOf(recordDefinition.getId()));
            pwr.print(FIELD_SEPARATOR);
            pwr.print(recordDefinition.getMsisdn());
            pwr.print(FIELD_SEPARATOR);
            pwr.print(recordDefinition.getProductId());
            pwr.print(FIELD_SEPARATOR);

            pwr.print(recordDefinition.getPackageId());
            pwr.print(FIELD_SEPARATOR);
            pwr.print(String.valueOf(recordDefinition.getOperationType()));
            pwr.print(FIELD_SEPARATOR);

            strBuf.delete(0, 40);
            pwr.print(sdf.format(recordDefinition.getOperationTime(), strBuf, FIELD_POS));
            pwr.print(FIELD_SEPARATOR);

            strBuf.delete(0, 40);

            pwr.print(sdf.format(recordDefinition.getEffectiveDate(), strBuf, FIELD_POS));
            strBuf.delete(0, 40);
            pwr.print(FIELD_SEPARATOR);

            pwr.print(sdf.format(recordDefinition.getDueDate(), strBuf, FIELD_POS));
            pwr.print('\n');

            if (0 == (i % RECORD_JOIN_SIZE)) {
                pwr.flush();
                i = 0;
                // Thread.yield();
            }

        }

        if (i > 0) {
            pwr.flush();
        }

        File file = null;

        // 处理当前文件中的最后一组记录
        if (recordCount < Config.RECORD_SAVE_CHUNK_SIZE) {
            pwr.close();
            file = new File(baseDir + '/' + recordDay + "/subspsync-gw-"
                    + FILE_INDEX_FORMATTER.format(targetFileIndex) + ".dat");
            printWriterMap.remove(fileKey);
        }
        return file;
    }

    public File finishRecords(String recordDay, int targetFileIndex) {
        String fileKey = recordDay + '-' + targetFileIndex;
        PrintWriter pwr = printWriterMap.get(fileKey);
        File file = null;
        if (null != pwr) {
            pwr.flush();
            pwr.close();
            file = new File(baseDir + '/' + recordDay + "/subspsync-gw-"
                    + FILE_INDEX_FORMATTER.format(targetFileIndex) + ".dat");
            printWriterMap.remove(fileKey);
        }
        return file;
    }

    public static void backupFile(final File file) {
        String recordDay = file.getParentFile().getName();
        File destFile = new File(baseDir + "/backup/" + recordDay);
        if (!destFile.exists()) {
            if (!destFile.mkdirs()) {
                return;
            }
        }
        destFile = new File(destFile, file.getName());
        try {
            Files.move(file.toPath(), destFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to backup file " + file);
        }
    }

    public static void purgeDir() {
        File[] dirs = new File(baseDir).listFiles();
        for (File dir : dirs) {
            if (dir.isDirectory() && 0 == dir.list().length) {
                dir.delete();
            }
        }
    }

}