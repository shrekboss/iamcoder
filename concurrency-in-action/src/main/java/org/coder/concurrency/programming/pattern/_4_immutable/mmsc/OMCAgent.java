package org.coder.concurrency.programming.pattern._4_immutable.mmsc;

/**
 * 与运维中心（Operation and Maintenance Center）对接的类<BR>
 * 模式角色：ImmutableObject.Manipulator
 */
public class OMCAgent extends Thread {

    @Override
    public void run() {
        boolean isTableModificationMsg = false;
        String updatedTableName = null;
        while (true) {
            // 省略其他代码
            /*
             * 从与OMC连接的Socket中读取消息并进行解析, 解析到数据表更新消息后,重置MMSCRouter实例。
             */
            if (isTableModificationMsg) {
                if ("MMSCInfo".equals(updatedTableName)) {
                    MMSCRouter.setInstance(new MMSCRouter());
                }
            }
            // 省略其他代码
        }
    }
}