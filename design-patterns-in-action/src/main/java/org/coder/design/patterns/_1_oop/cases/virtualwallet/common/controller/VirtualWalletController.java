package org.coder.design.patterns._1_oop.cases.virtualwallet.common.controller;

import org.coder.design.patterns._1_oop.cases.virtualwallet._anemic_domain_model.service.VirtualWalletService;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class VirtualWalletController {
    // 通过构造函数或者IOC框架注入
    private VirtualWalletService virtualWalletService;

    //查询余额
    public BigDecimal getBalance(Long walletId) {

        //...

        return BigDecimal.ZERO;
    }

    //出账
    public void debit(Long walletId, BigDecimal amount) {

        //...
    }

    //入账
    public void credit(Long walletId, BigDecimal amount) {

        //...
    }

    //转账
    public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {

        //...
    }

    //省略查询transaction的接口
}
