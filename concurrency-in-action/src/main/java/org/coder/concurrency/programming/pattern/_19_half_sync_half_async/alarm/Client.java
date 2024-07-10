package org.coder.concurrency.programming.pattern._19_half_sync_half_async.alarm;

import org.coder.concurrency.programming.pattern._11_two_phase_termination.alarm.AlarmMgr;
import org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm.AlarmType;
import org.coder.concurrency.programming.util.Debug;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Client {

    public static void main(String[] args) {
        test();
    }

    private static void test() {
        Debug.info("started.");

        final AlarmMgr alarmMgr = AlarmMgr.getInstance();
        alarmMgr.init();

        final String alarmId = "10000020";
        final String alarmExtraInfo = "name1=test;name2=value2";

        Thread t1;

        int count1 = 10;
        final Timer timer = new Timer(true);
        // end of run
        final CyclicBarrier cbr = new CyclicBarrier(count1, () -> timer.schedule(new TimerTask() {

            @Override
            public void run() {
                alarmMgr.sendAlarm(AlarmType.RESUME, alarmId, alarmExtraInfo);
                timer.cancel();
            }

        }, 50));

        for (int i = 0; i < count1; i++) {
            t1 = new Thread() {
                @Override
                public void run() {
                    try {
                        cbr.await();
                    } catch (InterruptedException e) {
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    for (int j = 0; j < 10; j++) {

                        alarmMgr.sendAlarm(AlarmType.FAULT, alarmId, alarmExtraInfo);
                    }
                }
            };
            t1.start();
        }

        // alarmMgr.disconnect();

        Random rnd = new Random();
        Timer timer1 = new Timer(true);
        timer1.schedule(new TimerTask() {

            @Override
            public void run() {
                (new Thread() {

                    @Override
                    public void run() {
                        for (int i = 0; i < 20; i++) {
                            alarmMgr.sendAlarm(AlarmType.FAULT, alarmId, alarmExtraInfo);
                        }
                    }

                }).start();

            }

        }, rnd.nextInt(150));

        timer1.schedule(new TimerTask() {

            @Override
            public void run() {
                alarmMgr.shutdown();
            }
        }, 5000);
    }
}