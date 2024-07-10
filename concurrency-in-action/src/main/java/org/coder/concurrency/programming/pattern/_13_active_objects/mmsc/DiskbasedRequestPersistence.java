package org.coder.concurrency.programming.pattern._13_active_objects.mmsc;

import org.apache.log4j.Logger;
import org.coder.concurrency.programming.util.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DiskbasedRequestPersistence implements RequestPersistence {
    // 负责缓存文件的存储管理
    final SectionBasedDiskStorage storage = new SectionBasedDiskStorage();
    final static Logger logger =
            Logger.getLogger(DiskbasedRequestPersistence.class);

    @Override
    public void store(MMSDeliverRequest request) {
        // 申请缓存文件的文件名
        String[] fileNameParts = storage.apply4Filename(request);
        File file = new File(fileNameParts[0]);
        try (ObjectOutputStream objOut = new ObjectOutputStream(
                new FileOutputStream(file));) {
            objOut.writeObject(request);
        } catch (IOException e) {
            storage.decrementSectionFileCount(fileNameParts[1]);
            logger.error("Failed to store request", e);
        }
    }

    class SectionBasedDiskStorage {
        private Deque<String> sectionNames = new LinkedList<String>();
        /*
         * Key->value: 存储子目录名->子目录下缓存文件计数器
         */
        private Map<String, AtomicInteger> sectionFileCountMap =
                new HashMap<>();
        private int maxFilesPerSection = 2000;
        private int maxSectionCount = 100;
        private String storageBaseDir = Tools.getWorkingDir("mmsc/tmp");
        private final Object sectionLock = new Object();

        public SectionBasedDiskStorage() {
            File dir = new File(storageBaseDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        public String[] apply4Filename(MMSDeliverRequest request) {
            String sectionName;
            int iFileCount;
            String oldestSectionName = null;
            String[] fileName = new String[2];
            synchronized (sectionLock) {
                // 获取当前的存储子目录名
                sectionName = getSectionName();
                AtomicInteger fileCount;
                fileCount = sectionFileCountMap.get(sectionName);
                iFileCount = fileCount.get();
                // 当前存储子目录已满
                if (iFileCount >= maxFilesPerSection) {
                    if (sectionNames.size() >= maxSectionCount) {
                        oldestSectionName = sectionNames.removeFirst();
                    }
                    // 创建新的存储子目录
                    sectionName = makeNewSectionDir();
                    fileCount = sectionFileCountMap.get(sectionName);

                }
                iFileCount = fileCount.addAndGet(1);

            }

            fileName[0] = storageBaseDir + "/" + sectionName + "/"
                    + new DecimalFormat("0000").format(iFileCount) + "-"
                    + request.getTimeStamp().getTime() / 1000 + "-"
                    + request.getExpiry() + ".rq";
            fileName[1] = sectionName;

            if (null != oldestSectionName) {
                // 删除最老的存储子目录
                this.removeSection(oldestSectionName);
            }

            return fileName;
        }

        public void decrementSectionFileCount(String sectionName) {
            AtomicInteger fileCount = sectionFileCountMap.get(sectionName);
            if (null != fileCount) {
                fileCount.decrementAndGet();
            }
        }

        private boolean removeSection(String sectionName) {
            boolean result = true;
            File dir = new File(storageBaseDir + "/" + sectionName);
            for (File file : dir.listFiles()) {
                result = result && file.delete();
            }
            result = result && dir.delete();
            return result;
        }

        private String getSectionName() {
            String sectionName;
            if (sectionNames.isEmpty()) {
                sectionName = this.makeNewSectionDir();

            } else {
                sectionName = sectionNames.getLast();
            }
            return sectionName;
        }

        private String makeNewSectionDir() {
            String sectionName;
            SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
            sectionName = sdf.format(new Date());
            File dir = new File(storageBaseDir + "/" + sectionName);
            if (dir.mkdir()) {
                sectionNames.addLast(sectionName);
                sectionFileCountMap.put(sectionName, new AtomicInteger(0));
            } else {
                throw new RuntimeException(
                        "Cannot create section dir " + sectionName);
            }

            return sectionName;
        }
    }// end of SectionBasedDiskStorage

    @Override
    public void close() throws IOException {
        // 什么也不做

    }
}