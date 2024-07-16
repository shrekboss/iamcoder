package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 3.4 Semaphore工具详解
 * Semaphore(信号量)是一个线程同步工具，主要用于在一个时刻允许多个线程对共享资源进行并行操作的场景。
 * 通常情况下，使用Semaphore的过程实际上是多个线程获取访问共享资源许可证的过程，下面是Semaphore的内部处理逻辑。
 * 1.如果此时Semaphore内部的计数器大于零，那么线程将可以获得小于该计数器数量的许可证，同时还会导致Semaphore内部的计数器减少所发放的许可证数量。
 * 2.如果此时Semaphore内部的计数器等于0，也就是说没有可用的许可证，那么当前线程有可能会被阻塞(使用tryAcquire方法时不会阻塞)。
 * 3.当线程不再使用许可证时，需要立即将其释放以供其他线程使用，所以建议将许可证的获取以及释放动作写在try..finally语句块中。
 * <p>
 * 3.4.1 Semaphore限制同时在线的用户数量
 * 了解了Semaphore的工作流程以及原理之后，我们再来看看Semaphore该如何使用，并且适用用于何种场景之下。
 * 在本书的示例代码中，我们模拟某个登录系统，最多限制给定数量的人员同时在线，如果所能申请的许可证不足，
 * 那么将告诉用户无法登录，稍后重试。
 * <p>
 * 在下面的代码中，我们定义了Semaphore的许可证数量为10，这就意味着当前的系统最多只能有10个用户同时在线，
 * 如果其他线程在Semaphore许可证数量为0的时候尝试申请，就将会出现申请不成功的情况。
 * <p>
 * 如果将tryAcquire方法修改为阻塞方法acquire，那么我们会看到所有的未登录成功的用户在其他用户退出系统后会陆陆续续登录成功(修改后的login方法)。
 * <p>
 * 为了节约篇幅，读者可以自行修改代码并且检查程序的执行结果，这里就不再赘述了。
 */
public class SemaphoreExample1 {

    public static void main(String[] args) {
        //定义许可证数量，最多同时只能有10个用户登录成功并且在线
        final int MAX_PERMIT_LOGIN_ACCOUNT = 10;

        final LoginService loginService = new LoginService(MAX_PERMIT_LOGIN_ACCOUNT);

        //启动20个线程
        IntStream.range(0, 20).forEach(i -> new Thread(() -> {
            //登录系统，实际上是一次许可证的获取操作
            boolean login = loginService.login();
            //如果登录失败，则不再进行其他操作
            if (!login) {
                System.out.println(Thread.currentThread() + " is refused due to exceed max online account.");
                return;
            }

            try {
                //简单模拟登录成功后的系统操作
                simulateWork();
            } finally {
                //退出系统，实际上是对许可证资源的释放
                loginService.logout();
            }
        }, "User-" + i).start());
    }

    //随机休眠
    private static void simulateWork() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class LoginService {
        private final Semaphore semaphore;

        public LoginService(int maxPermitLoginAccount) {
            //初始化Semaphore
            this.semaphore = new Semaphore(maxPermitLoginAccount, true);
        }

        public boolean login() {
            //获取许可证，如果获取失败该方法会返回false，tryAcquire 不是一个阻塞方法
            boolean login = semaphore.tryAcquire();

            if (login) {
                System.out.println(Thread.currentThread() + " login success.");
            }
            return login;
        }

        public boolean login2() {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread() + " login success.");
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        //释放许可证
        public void logout() {
            semaphore.release();
            System.out.println(Thread.currentThread() + " logout success.");
        }
    }
}