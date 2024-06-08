package org.coder.design.patterns._1_oop.cases.virtualwallet._anemic_domain_model.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 基于贫血模型(Anemic Domain Model)的传统开发模式
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class VirtualWalletBo {

    private Long id;
    private Long createTime;
    private BigDecimal balance;
}
