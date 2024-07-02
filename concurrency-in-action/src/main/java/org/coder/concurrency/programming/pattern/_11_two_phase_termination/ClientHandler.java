package org.coder.concurrency.programming.pattern._11_two_phase_termination;

import org.coder.concurrency.programming.pattern._11_two_phase_termination.reference.SocketCleaningTracker;

import java.io.*;
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
public class ClientHandler implements Runnable {


    private final Socket socket;

    private final String clientIdentify;

    public ClientHandler(final Socket socket) {
        this.socket = socket;
        this.clientIdentify = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public void run() {
        try {
            this.chat();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.release();
        }
    }

    private void release() {
        try {
            if (socket != null) {
//                socket.close();
                SocketCleaningTracker.track(socket);
            }
        } catch (Throwable e) {
            //ignore
            // 为了使客户端的关闭不影响线程任务的结束，捕获了 Throwable 异常(在关闭客户端连接时出现的异常，视为不可恢复的异常，基本上没有
            // 针对该异常进行处理的必要)
        }
    }

    private void chat() throws IOException {
        BufferedReader bufferedReader = wrap2Reader(this.socket.getInputStream());
        PrintStream printStream = wrap2Print(this.socket.getOutputStream());
        String received;
        while ((received = bufferedReader.readLine()) != null) {

            System.out.printf("client:%s-message:%s\n", clientIdentify, received);
            if (received.equals("quit")) {
                write2Client(printStream, "client will close");
                socket.close();
                break;
            }
            write2Client(printStream, "Server:" + received);
        }
    }

    private BufferedReader wrap2Reader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private PrintStream wrap2Print(OutputStream outputStream) {
        return new PrintStream(outputStream);
    }

    private void write2Client(PrintStream print, String message) {
        print.println(message);
        print.flush();
    }
}
