package org.coder.concurrency.programming.pattern._19_half_sync_half_async.alarm;

import org.coder.concurrency.programming.pattern._11_two_phase_termination.alarm.AlarmMgr;
import org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm.AlarmType;

public class CaseRunner {

    public static void main(String[] args) throws InterruptedException {
        AlarmMgr alarmMgr = AlarmMgr.getInstance();
        alarmMgr.init();

        String alarmId = "0000000010";

        alarmMgr.sendAlarm(AlarmType.FAULT, alarmId, "key1=value1;key2=value2");

        Thread.sleep(80);

        alarmMgr.sendAlarm(AlarmType.RESUME, alarmId,
                "key1=value1;key2=value2");
        Thread.sleep(600);

        alarmMgr.shutdown();
    }

}