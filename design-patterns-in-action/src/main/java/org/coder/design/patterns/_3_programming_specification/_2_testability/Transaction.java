package org.coder.design.patterns._3_programming_specification._2_testability;

import lombok.Getter;
import org.coder.design.patterns._3_programming_specification._2_testability.simulate.IdGenerator;
import org.coder.design.patterns._3_programming_specification._2_testability.simulate.STATUS;
import org.coder.design.patterns._3_programming_specification._2_testability.simulate.WalletRpcService;

import javax.transaction.InvalidTransactionException;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Getter
public class Transaction {
    private String id;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private String orderId;
    private Long createTimestamp;
    private Double amount;
    private STATUS status;
    private String walletTransactionId;

    // 变更点：针对 walletRpcService 服务
    private WalletRpcService walletRpcService;

    public void setWalletRpcService(WalletRpcService walletRpcService) {
        this.walletRpcService = walletRpcService;
    }

    // 变更点：针对单例的 RedisDistributedLock 服务
    private TransactionLock transactionLock;

    public void setTransactionLock(TransactionLock transactionLock) {
        this.transactionLock = transactionLock;
    }

    // 变更点: 代码中包含跟"时间"有关的"未决行为"逻辑封装到 isExpired() 函数中即可。
    // 针对 createTimestamp 属性没有对应的 setter 方法
    public boolean isExpired() {
        long executionInvokedTimestamp = System.currentTimeMillis();
        return (executionInvokedTimestamp - createTimestamp) / 86400000 > 14;
    }

    /**
     * 构造函数中并非只包含简单赋值操作。交易 id 的赋值逻辑稍微复杂。我们最好也要测试一下，以保证这部分逻辑的正确性。
     */
    public Transaction(String preAssignedId, Long buyerId, Long sellerId, Long productId, String orderId) {
//        if (preAssignedId != null && !preAssignedId.isEmpty()) {
//            this.id = preAssignedId;
//        } else {
//            this.id = IdGenerator.generateTransactionId();
//        }
//        if (!this.id.startsWith("t_")) {
//            this.id = "t_" + preAssignedId;
//        }
        // 变更点
        fillTransactionId(preAssignedId);

        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.orderId = orderId;
        this.status = STATUS.TO_BE_EXECUTED;
        this.createTimestamp = System.currentTimeMillis();
    }

    protected void fillTransactionId(String preAssignedId) {
        if (preAssignedId != null && !preAssignedId.isEmpty()) {
            this.id = preAssignedId;
        } else {
            this.id = IdGenerator.generateTransactionId();
        }
        if (!this.id.startsWith("t_")) {
            this.id = "t_" + preAssignedId;
        }
    }

    public boolean execute() throws InvalidTransactionException {
        if (buyerId == null || (sellerId == null || amount < 0.0)) {
            throw new InvalidTransactionException(/*...*/);
        }
        if (status == STATUS.EXECUTED) return true;
        boolean isLocked = false;
        try {
            // 变更点，_simulate 获取分布式锁
            isLocked = transactionLock.lock(id);
            if (!isLocked) {
                return false; // 锁定未成功，返回false，job兜底执行
            }
            if (status == STATUS.EXECUTED) return true; // double check

            // 变更点
            // if ((executionInvokedTimestamp - createTimestamp) / 86400000  > 14) {
            if (isExpired()) {
                this.status = STATUS.EXPIRED;
                return false;
            }

            // 变更点：删除下面这一行代码
            //  WalletRpcService walletRpcService = new WalletRpcService();
            String walletTransactionId = walletRpcService.moveMoney(id, buyerId, sellerId, amount);

            if (walletTransactionId != null) {
                this.walletTransactionId = walletTransactionId;
                this.status = STATUS.EXECUTED;
                return true;
            } else {
                this.status = STATUS.FAILED;
                return false;
            }
        } finally {
            if (isLocked) {
                // 变更点，_simulate 释放分布式锁
                transactionLock.unlock();
            }
        }
    }
}
