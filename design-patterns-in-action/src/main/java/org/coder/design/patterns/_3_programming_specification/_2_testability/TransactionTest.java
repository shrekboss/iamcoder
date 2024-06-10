package org.coder.design.patterns._3_programming_specification._2_testability;

import org.coder.design.patterns._3_programming_specification._2_testability.mock.STATUS;
import org.junit.Test;

import javax.transaction.InvalidTransactionException;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TransactionTest {

    /**
     * 测试 1：涉及 Redis 服务和 Wallet RPC 服务的依赖
     */
    @Test
    public void testExecute() throws InvalidTransactionException {
        Long buyerId = 123L;
        Long sellerId = 234L;
        Long productId = 345L;
        String orderId = "456";

        TransactionLock mockLock = new TransactionLock() {

            @Override
            public boolean lock(String id) {
                return true;
            }

            @Override
            public boolean unlock() {
                return super.unlock();
            }
        };

        Transaction transaction = new Transaction(null, buyerId, sellerId, productId, orderId);

        // 使用mock对象来替代真正的 RPC 服务和 RedisDistributedLock 服务
        transaction.setWalletRpcService(new MockWalletRpcServiceOne());
        transaction.setTransactionLock(mockLock);

        boolean executedResult = transaction.execute();
        assertTrue(executedResult);
        assertEquals(STATUS.EXECUTED, transaction.getStatus());
    }

    /**
     * 没有针对 createTimestamp 的 set 方法
     */
    @Test
    public void testExecute_with_TransactionIsExpired() throws InvalidTransactionException {
        Long buyerId = 123L;
        Long sellerId = 234L;
        Long productId = 345L;
        String orderId = "456";
        Transaction transaction = new Transaction(null, buyerId, sellerId, productId, orderId) {

            @Override
            public boolean isExpired() {
                return true;
            }
        };
        // transaction.setCreateTimestamp(System.currentTimeMillis() / 86400000 - 14);

        boolean actualResult = transaction.execute();
        assertFalse(actualResult);
        assertEquals(STATUS.EXPIRED, transaction.getStatus());
    }
}
