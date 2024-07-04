package org.coder.concurrency.programming.pattern._11_two_phase_termination.alarm;

import org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm.AlarmInfo;
import org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm.AlarmType;
import org.coder.concurrency.programming.util.Debug;

/**
 * 告警功能入口类。 模式角色：HalfSync/HalfAsync.AsyncTask
 * 模式角色：Two-phaseTermination.ThreadOwner
 */
public class AlarmMgr {
    // 保存AlarmMgr类的唯一实例
    private static final AlarmMgr INSTANCE = new AlarmMgr();

    private volatile boolean shutdownRequested = false;

    // 告警发送线程
    // 不被其他代码误停止，只能调用 shutdown() 方法来请求该线程停止
    private final AlarmSendingThread alarmSendingThread;

    // 私有构造器
    private AlarmMgr() {
        alarmSendingThread = new AlarmSendingThread();

    }

    // 返回类AlarmMgr的唯一实例
    public static AlarmMgr getInstance() {
        return INSTANCE;
    }

    /**
     * 发送告警
     *
     * @param type      告警类型
     * @param id        告警编号
     * @param extraInfo 告警参数
     * @return 由type+id+extraInfo唯一确定的告警信息被提交的次数。-1表示告警管理器已被关闭。
     */
    public int sendAlarm(AlarmType type, String id, String extraInfo) {
        Debug.info("Trigger alarm " + type + "," + id + ',' + extraInfo);
        int duplicateSubmissionCount = 0;
        try {
            AlarmInfo alarmInfo = new AlarmInfo(id, type);
            alarmInfo.setExtraInfo(extraInfo);
            duplicateSubmissionCount = alarmSendingThread.sendAlarm(alarmInfo);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return duplicateSubmissionCount;
    }

    public void init() {
        alarmSendingThread.start();
    }

    public synchronized void shutdown() {
        if (shutdownRequested) {
            throw new IllegalStateException("shutdown already requested!");
        }

        alarmSendingThread.terminate();
        shutdownRequested = true;
    }

}