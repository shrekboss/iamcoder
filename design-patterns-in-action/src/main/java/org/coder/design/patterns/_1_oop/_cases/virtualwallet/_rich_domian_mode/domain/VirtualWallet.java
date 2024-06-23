package org.coder.design.patterns._1_oop._cases.virtualwallet._rich_domian_mode.domain;

import org.coder.design.patterns._1_oop._cases.virtualwallet.common.exception.InsufficientBalanceException;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.exception.InvalidAmountException;

import java.math.BigDecimal;

/**
 * Domain领域模型(充血模型)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class VirtualWallet {
    private Long id;
    private Long createTime = System.currentTimeMillis();
    private BigDecimal balance = BigDecimal.ZERO;

    public VirtualWallet(Long preAllocatedId) {
        this.id = preAllocatedId;
    }

    // =============================== 新增功能 ===============================
    private boolean isAllowedOverdraft = true;
    private BigDecimal overdraftAmount = BigDecimal.ZERO;
    private BigDecimal frozenAmount = BigDecimal.ZERO;

    public void freeze(BigDecimal amount) {
    }

    public void unfreeze(BigDecimal amount) {
    }

    public void increaseOverdraftAmount(BigDecimal amount) {
    }

    public void decreaseOverdraftAmount(BigDecimal amount) {
    }

    public void closeOverdraft() {
    }

    public void openOverdraft() {
    }
    // =============================== 新增功能 ===============================



    public BigDecimal balance() {
        return this.balance;
    }

    public BigDecimal getAvailableBalance() {
        BigDecimal totalAvaliableBalance = this.balance.subtract(this.frozenAmount);
        if (isAllowedOverdraft) {
            totalAvaliableBalance = totalAvaliableBalance.add(this.overdraftAmount);
        }
        return totalAvaliableBalance;
    }

    public void debit(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException();
        }
        this.balance = this.balance.add(amount);
    }
}


