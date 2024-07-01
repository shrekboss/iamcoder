package org.coder.concurrency.programming.pattern._10_thread_per_message.chat;

import org.coder.concurrency.programming.thread.threadpool.BasicThreadPool;
import org.coder.concurrency.programming.thread.threadpool.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ChatServer {

    private final int port;

    private ThreadPool threadPool;

    private ServerSocket serverSocket;

    public ChatServer(int port) {
        this.port = port;
    }

    public ChatServer() {
        this(13312);
    }

    public void startServer() throws IOException {

        this.threadPool = new BasicThreadPool(1, 4, 2, 1000);
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setReuseAddress(true);
        System.out.println("Chat server is started and listen at port: " + port);
        this.listen();
    }

    private void listen() throws IOException {
        for (; ; ) {
            // accept 方法是阻塞方法，当有新的连接进入时才会返回，并且返回的是客户端的连接
            Socket client = serverSocket.accept();
            // 将客户端连接作为一个 Request 封装成对应的 Handler 然后提交给线程池
            System.out.println("The client " + client + " connected.");
            this.threadPool.execute(new ClientHandler(client));
        }
    }
}
