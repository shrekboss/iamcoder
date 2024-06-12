package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton.cases._1_resource_confic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public final class Logger {
    private FileWriter writer;

    // 饿汉式的单例模式
    private static final Logger instance = new Logger();

    private Logger() {
        File file = new File("/Users/crayzer/workspaces/iamcoder/log.txt");
        try {
            // true表示追加写入，
            writer = new FileWriter(file, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Logger getInstance() {
        return instance;
    }

    public void log(String message) {
        try {
            writer.write(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}




