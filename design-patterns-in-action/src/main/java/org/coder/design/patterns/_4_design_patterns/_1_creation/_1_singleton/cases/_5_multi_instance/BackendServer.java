package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton.cases._5_multi_instance;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 实现多例模式
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class BackendServer {

    private long serverNo;
    private String serverAddress;

    private static final int SERVER_COUNT = 3;
    private static final Map<Long, BackendServer> serverInstances = new HashMap<>();

    static {
        serverInstances.put(1L, new BackendServer(1L, "192.134.22.138:8080"));
        serverInstances.put(2L, new BackendServer(2L, "192.134.22.139:8080"));
        serverInstances.put(3L, new BackendServer(3L, "192.134.22.140:8080"));
    }

    private BackendServer(long serverNo, String serverAddress) {
        this.serverNo = serverNo;
        this.serverAddress = serverAddress;
    }

    public static BackendServer getInstance(long serverNo) {
        return serverInstances.get(serverNo);
    }

    public static BackendServer getRandomInstance() {
        Random r = new Random();
        long no = r.nextInt(SERVER_COUNT) + 1;
        return serverInstances.get(no);
    }

    public static void main(String[] args) {
        System.out.println(BackendServer.getInstance(1L));
        System.out.println(BackendServer.getInstance(2L));
        System.out.println(BackendServer.getInstance(3L));
        System.out.println(BackendServer.getInstance(4L));

        System.out.println();

        System.out.println(BackendServer.getRandomInstance());
        System.out.println(BackendServer.getRandomInstance());
        System.out.println(BackendServer.getRandomInstance());
    }
}
