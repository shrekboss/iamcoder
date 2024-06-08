package org.coder.design.patterns._1_oop.cases.virtualwallet.common.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class VirtualWalletEntity {

    private Long id;
    private BigDecimal balance;
}
