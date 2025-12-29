package org.coder.concurrency.programming.pattern._10_thread_per_message.chat;

import java.io.IOException;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ChatServerTest {

    public static void main(String[] args) throws IOException {
        new ChatServer().startServer();
    }
}
