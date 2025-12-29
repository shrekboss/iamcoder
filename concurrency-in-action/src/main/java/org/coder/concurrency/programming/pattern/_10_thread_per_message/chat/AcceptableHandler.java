package org.coder.concurrency.programming.pattern._10_thread_per_message.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class AcceptableHandler implements Runnable {
    private final SelectionKey selectionKey;

    private final Selector selector;

    public AcceptableHandler(SelectionKey selectionKey,
                             Selector selector) {
        this.selectionKey = selectionKey;
        this.selector = selector;
    }

    @Override
    public void run() {
        if (!selectionKey.isAcceptable())
            return;
        try {
            ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel clientChannel = channel.accept();
            System.out.printf("Received the client %s\n", clientChannel.socket().getInetAddress());
            clientChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            clientChannel.register(selector, SelectionKey.OP_READ |
                    SelectionKey.OP_WRITE, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
