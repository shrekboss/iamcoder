package org.coder.concurrency.programming.juc._2_atomic;

/**
 * 2.4.1 AtomicReference的应用场景
 * 这里通过设计一个个人银行账号资金变化的场景，逐渐引入AtomicReference的使用，该实例有些特殊，需要满足如下几点要求。
 * 1.个人账号被设计为不可变对象，一旦创建就是无法进行修改。
 * 2.个人账号类只包含两个字段：账号名、现金数字。
 * 3.为了便于验证，我们约定个人账号的现金只能增多而不能减少。
 * <p>
 * 根据前两个要求，我们简单设计一个代表个人银行账号的Java类DebitCard，该类将被设计为不可变。
 */
public class DebitCard {

    private final String account;
    private final int amount;

    public DebitCard(String account, int amount) {
        this.account = account;
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "DebitCard [account=" + account + ", amount=" + amount + "]";
    }
}