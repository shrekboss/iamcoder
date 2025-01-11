package org.coder.design.patterns._1_oop._cases.virtualwallet._rich_domian_mode.service;

import org.coder.design.patterns._1_oop._cases.virtualwallet._rich_domian_mode.domain.VirtualWallet;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.TransactionType;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.entity.VirtualWalletEntity;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.entity.VirtualWalletTransactionEntity;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.repository.VirtualWalletRepository;
import org.coder.design.patterns._1_oop._cases.virtualwallet.common.repository.VirtualWalletTransactionRepository;

import java.math.BigDecimal;

public class VirtualWalletService {
    // 通过构造函数或者IOC框架注入
    private VirtualWalletRepository walletRepo;
    private VirtualWalletTransactionRepository transactionRepo;

    public VirtualWallet getVirtualWallet(Long walletId) {
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        VirtualWallet wallet = convert(walletEntity);
        return wallet;
    }

    public BigDecimal getBalance(Long walletId) {
        return walletRepo.getBalance(walletId);
    }

    //    @Transactional
    public void debit(Long walletId, BigDecimal amount) {
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        VirtualWallet wallet = convert(walletEntity);
        wallet.debit(amount);
        VirtualWalletTransactionEntity transactionEntity = new VirtualWalletTransactionEntity();
        transactionEntity.setAmount(amount);
        transactionEntity.setCreateTime(System.currentTimeMillis());
        transactionEntity.setType(TransactionType.DEBIT);
        transactionEntity.setFromWalletId(walletId);
        transactionRepo.saveTransaction(transactionEntity);
        walletRepo.updateBalance(walletId, wallet.balance());
    }

    //    @Transactional
    public void credit(Long walletId, BigDecimal amount) {
        VirtualWalletEntity walletEntity = walletRepo.getWalletEntity(walletId);
        VirtualWallet wallet = convert(walletEntity);
        wallet.credit(amount);
        VirtualWalletTransactionEntity transactionEntity = new VirtualWalletTransactionEntity();
        transactionEntity.setAmount(amount);
        transactionEntity.setCreateTime(System.currentTimeMillis());
        transactionEntity.setType(TransactionType.CREDIT);
        transactionEntity.setFromWalletId(walletId);
        transactionRepo.saveTransaction(transactionEntity);
        walletRepo.updateBalance(walletId, wallet.balance());
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

    private VirtualWallet convert(VirtualWalletEntity walletEntity) {
        return null;
    }
}