## 代码的可测试性

> 一个电商系统的交易类，用来记录每笔订单交易的情况。Transaction 类中的 execute() 函数负责执行转账操作，将钱从买家的钱包转到卖家的钱包中。真正的转账操作是通过调用
> WalletRpcService RPC 服务来完成的。除此之外，代码中还涉及一个分布式锁 DistributedLock 单例类，用来避免 Transaction
> 并发执行，导致用户的钱被重复转出。

初始代码如下：

```java
public class Transaction {
    private String id;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private String orderId;
    private Long createTimestamp;
    private Double amount;
    private STATUS status;
    private String walletTransactionId;

    // ...get() methods...

    public Transaction(String preAssignedId, Long buyerId, Long sellerId, Long productId, String orderId) {
        if (preAssignedId != null && !preAssignedId.isEmpty()) {
            this.id = preAssignedId;
        } else {
            this.id = IdGenerator.generateTransactionId();
        }
        if (!this.id.startWith("t_")) {
            this.id = "t_" + preAssignedId;
        }
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.orderId = orderId;
        this.status = STATUS.TO_BE_EXECUTD;
        this.createTimestamp = System.currentTimestamp();
    }

    public boolean execute() throws InvalidTransactionException {
        if (buyerId == null || (sellerId == null || amount < 0.0)) {
            throw new InvalidTransactionException(/*...*/);
        }
        if (status == STATUS.EXECUTED) return true;
        boolean isLocked = false;
        try {
            isLocked = RedisDistributedLock.getSingletonIntance().lockTransction(id);
            if (!isLocked) {
                return false; // 锁定未成功，返回false，job兜底执行
            }
            if (status == STATUS.EXECUTED) return true; // double check
            long executionInvokedTimestamp = System.currentTimestamp();
            if ((executionInvokedTimestamp - createTimestamp) / 86400000 > 14) {
                this.status = STATUS.EXPIRED;
                return false;
            }
            WalletRpcService walletRpcService = new WalletRpcService();
            String walletTransactionId = walletRpcService.moveMoney(id, buyerId, sellerId, amount);
            if (walletTransactionId != null) {
                this.walletTransactionId = walletTransactionId;
                this.status = STATUS.EXECUTED;
                return true;
            } else {
                this.status = STATUS.FAILED;
                return false;
            }
        } finally {
            if (isLocked) {
                RedisDistributedLock.getSingletonIntance().unlockTransction(id);
            }
        }
    }
}
```

设计了下面 6 个测试用例:

- 正常情况下，交易执行成功，回填用于对账（交易与钱包的交易流水）用的 walletTransactionId，交易状态设置为 EXECUTED，函数返回
  true。
    - TransactionTest.testExecute()
    - 针对 walletRpcService 服务
        - [MockWalletRpcServiceOne.java](MockWalletRpcServiceOne.java)
        - [MockWalletRpcServiceTwo.java](MockWalletRpcServiceTwo.java)
    - 针对单例的 RedisDistributedLock 服务
        - [TransactionLock.java](TransactionLock.java)
    - [Transaction.java](Transaction.java)
- buyerId、sellerId 为 null、amount 小于 0，返回 InvalidTransactionException。
    - 略
- 交易已过期（createTimestamp 超过 14 天），交易状态设置为 EXPIRED，返回 false。
    - 构造函数中并非只包含简单赋值操作。交易 id 的赋值逻辑稍微复杂。我们最好也要测试一下，以保证这部分逻辑的正确性。
    - 代码中包含跟“时间”有关的“未决行为”逻辑封装到 isExpired() 函数中即可。
    - TransactionTest.testExecute_with_TransactionIsExpired
    - [Transaction.java](Transaction.java)
- 交易已经执行了（status==EXECUTED），不再重复执行转钱逻辑，返回 true。
    - 略
- 钱包（WalletRpcService）转钱失败，交易状态设置为 FAILED，函数返回 false。
    - 略
- 交易正在执行着，不会被重复执行，函数直接返回 false。
    - 略

## 全局变量

> 全局变量是一种面向过程的编程风格，有种种弊端。实际上，滥用全局变量也让编写单元测试变得困难。

参考代码：[RangeLimiterTest.java](RangeLimiterTest.java)

- 上面的单元测试有可能会运行失败。
    - 假设单元测试框架顺序依次执行两个测试用例。
    - 在第一个测试用例执行完成之后，position 的值变成了 -1；
    - 再执行第二个测试用例的时候，position 变成了 5，move() 函数返回 true，assertFalse 语句判定失败。
    - 所以，第二个测试用例运行失败。

当然，如果 RangeLimiter 类有暴露重设（reset）position 值的函数，可以在每次执行单元测试用例之前，把 position 重设为
0，这样就能解决刚刚的问题。

不过，每个单元测试框架执行单元测试用例的方式可能是不同的。有的是顺序执行，有的是并发执行。对于并发执行的情况，即便我们每次都把
position 重设为 0，也并不奏效。如果两个测试用例并发执行，第 16、17、18、23 这四行代码可能会交叉执行，影响到 move() 函数的执行结果。