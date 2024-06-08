package org.coder.design.patterns._1_oop.cases.virtualwallet.common.entity;

import lombok.Data;
import org.coder.design.patterns._1_oop.cases.virtualwallet.common.TransactionType;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class VirtualWalletTransactionEntity {

    private BigDecimal amount;
    private long createTime;
    private TransactionType type;
    private Long fromWalletId;
    private Long toWalletId;

}
