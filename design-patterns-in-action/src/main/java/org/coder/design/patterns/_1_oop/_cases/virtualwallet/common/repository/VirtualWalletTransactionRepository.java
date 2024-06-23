package org.coder.design.patterns._1_oop._cases.virtualwallet.common.repository;

import org.coder.design.patterns._1_oop._cases.virtualwallet.common.Status;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.entity.VirtualWalletTransactionEntity;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class VirtualWalletTransactionRepository {
    public void updateStatus(Long transactionId, Status closed) {
    }

    public Long saveTransaction(VirtualWalletTransactionEntity transactionEntity) {
        return null;
    }
}
