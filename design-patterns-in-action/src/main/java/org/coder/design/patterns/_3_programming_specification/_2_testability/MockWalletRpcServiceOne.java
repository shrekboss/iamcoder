package org.coder.design.patterns._3_programming_specification._2_testability;

import org.coder.design.patterns._3_programming_specification._2_testability.simulate.WalletRpcService;

/**
 * Mock {@link WalletRpcService } moveMoney() 方法实现
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class MockWalletRpcServiceOne extends WalletRpcService {
    public String moveMoney(Long id, Long fromUserId, Long toUserId, Double amount) {
        return "123bac";
    }
}
