package org.coder.design.patterns._1_oop._cases.virtualwallet._anemic_domain_model.service;

import org.coder.design.patterns._1_oop._cases.virtualwallet._anemic_domain_model.bo.VirtualWalletBo;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.TransactionType;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.entity.VirtualWalletEntity;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.entity.VirtualWalletTransactionEntity;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.exception.NoSufficientBalanceException;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.repository.VirtualWalletRepository;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.repository.VirtualWalletTransactionRepository;

import java.math.BigDecimal;

/**
 * 基于贫血模型(Anemic Domain Model)的传统开发模式
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class VirtualWalletService {
    // 通过构造函数或者IOC框架注入
    private VirtualWalletRepository walletRepo;
    private VirtualWalletTransactionRepository transactionRepo;

    public VirtualWalletBo getVirtualWallet(Long walletId) {
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);

        return convert(walletEntity);
    }

    public BigDecimal getBalance(Long walletId) {
        return walletRepo.getBalance(walletId);
    }

    //    @Transactional
    public void debit(Long walletId, BigDecimal amount) {
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        BigDecimal balance = walletEntity.getBalance();
        if (balance.compareTo(amount) < 0) {
            throw new NoSufficientBalanceException();
        }
        VirtualWalletTransactionEntity transactionEntity = new VirtualWalletTransactionEntity();
        transactionEntity.setAmount(amount);
        transactionEntity.setCreateTime(System.currentTimeMillis());
        transactionEntity.setType(TransactionType.DEBIT);
        transactionEntity.setFromWalletId(walletId);
        transactionRepo.saveTransaction(transactionEntity);
        walletRepo.updateBalance(walletId, balance.subtract(amount));
    }

    //    @Transactional
    public void credit(Long walletId, BigDecimal amount) {
        VirtualWalletTransactionEntity transactionEntity = new VirtualWalletTransactionEntity();
        transactionEntity.setAmount(amount);
        transactionEntity.setCreateTime(System.currentTimeMillis());
        transactionEntity.setType(TransactionType.CREDIT);
        transactionEntity.setFromWalletId(walletId);
        transactionRepo.saveTransaction(transactionEntity);
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        BigDecimal balance = walletEntity.getBalance();
        walletRepo.updateBalance(walletId, balance.add(amount));
    }

    //    @Transactional
    public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
        VirtualWalletTransactionEntity transactionEntity = new VirtualWalletTransactionEntity();
        transactionEntity.setAmount(amount);
        transactionEntity.setCreateTime(System.currentTimeMillis());
        transactionEntity.setType(TransactionType.TRANSFER);
        transactionEntity.setFromWalletId(fromWalletId);
        transactionEntity.setToWalletId(toWalletId);
        transactionRepo.saveTransaction(transactionEntity);
        debit(fromWalletId, amount);
        credit(toWalletId, amount);
    }

    private VirtualWalletBo convert(VirtualWalletEntity walletEntity) {
        return null;
    }
}