package org.coder.design.patterns._1_oop.cases.virtualwallet.common.repository;

import org.coder.design.patterns._1_oop.cases.virtualwallet.common.entity.VirtualWalletEntity;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class VirtualWalletRepository {
    public VirtualWalletEntity getWalletEntity(Long walletId) {
        return null;
    }

    public BigDecimal getBalance(Long walletId) {
        return null;
    }

    public void updateBalance(Long walletId, BigDecimal subtract) {
    }
}
