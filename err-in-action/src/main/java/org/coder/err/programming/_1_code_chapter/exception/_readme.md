## 异常处理：别让自己在出问题的时候变为瞎子

### 1. 捕获和处理异常容易犯的错

参考代码：[handleexception](handleexception)

<img src="http://ww1.sinaimg.cn/large/002eBIeDgy1gu70fxpy4wj61080zogob02.jpg" alt="undefined" style="zoom:50%;" />

#### 不在业务代码层面考虑异常处理，仅在框架层面粗犷捕获和处理异常

- 每层架构的工作性质不同，且从业务性质上异常可能分为业务异常和系统异常两大类，这就决定了很难进行
  统一的异常处理。(不建议在框架层面进行异常的自动、统一处理，尤其不要随意捕获异常。但，框架可以做兜底工作。)
    - Repository 层出现异常或许可以忽略，或许可以降级，或许需要转化为一个友好的异常
      。如果一律捕获异常仅记录日志，很可能业务逻辑已经出错，而用户和程序本身完全感知不到。
    - Service 层往往涉及数据库事务，出现异常同样不适合捕获，否则事务无法自动回滚。此外 Service 层
      涉及业务逻辑，有些业务逻辑执行中遇到业务异常，可能需要在异常后转入分支业务流程。如果业务异常都被框架捕获了，业务功能就会不正常。
    - 如果下层异常上升到 Controller 层还是无法处理的话，Controller 层往往会给予用户友好提示，或是根据每一个 API
      的异常表返回指定的异常类型，同样无法对所有异常一视同仁。
    - 如果异常上升到最上层逻辑还是无法处理的话，可以以统一的方式进行异常转换，比如通过 @RestControllerAdvice +
      @ExceptionHandler，来捕获这些“未处理”异常：
        - 对于自定义的业务异常，以 Warn 级别的日志记录异常以及当前 URL、执行方法等信息后，提取异常中的错误码和消息等信息，转换为合适的
          API 包装体返回给 API 调用方；
        - 对于无法处理的系统异常，以 Error 级别的日志记录异常和上下文信息（比如 URL、参数、用户
          ID）后，转换为普适的“服务器忙，请稍后再试”异常信息，同样以 API 包装体返回给调用方。

#### 捕获了异常后直接生吞

> 通常情况下，生吞异常的原因，可能是不希望自己的方法抛出受检异常，只是为了把异常“处理掉”而捕获并生吞异常，也可能是想当然地认为异常并不重要或不可能产生。但不管是什么原因，不管是你认为多么不重要的异常，都不应该生吞，哪怕是一个日志也好。

#### 丢弃异常的原始信息

#### 抛出异常时不指定任何消息

#### 除了通过日志正确记录异常原始信息外，通常还有三种处理模式

- 转换，即转换新的异常抛出。对于新抛出的异常，最好具有特定的分类和明确的异常消息，而不是随便抛一
  个无关或没有任何信息的异常，并最好通过 cause 关联老异常。
- 重试，即重试之前的操作。比如远程调用服务端过载超时的情况，盲目重试会让问题更严重，需要考虑当前
  情况是否适合重试。
- 恢复，即尝试进行降级处理，或使用默认值来替代原始数据。

### 2. 小心finally中的异常

参考代码：[finallyissue](finallyissue)

虽然 try 中的逻辑出现了异常，但却被 finally 中的异常覆盖了（为什么被覆盖，原因也很简单，因为一个方法无法出现两个异常。）

- finally 代码块自己负责异常捕获和处理
- 把 try 中的异常作为主异常抛出，使用 addSuppressed 方法把 finally 中的异常附加到主异常上(正是
  try-with-resources 语句的做法)

对于实现了 AutoCloseable 接口的资源，建议使用 try-with-resources 来释放资源，否则也可能会产生刚才提到的，释放资源时出现的异常覆盖主异常的问题

### 3. 千万别把异常定义为静态变量

参考代码：[predefinedexception](predefinedexception)

异常定义为静态变量会导致异常信息固化，这就和异常的栈一定是需要根据当前调用来动态获取相矛盾

```java
public class Exceptions {
    public static BusinessException ORDEREXISTS = new BusinessException("订单已经存在", 3001);
	...
}
```

### 4. 提交线程池的任务出了异常会怎么样？

参考代码：[threadpoolandexception](threadpoolandexception)

#### execute 方式

通过 execute 提交，那么出现异常会导致线程退出，大量的异常会导致线程重复创建引起性能问题
，我们应该尽可能确保任务不出异常，同时设置默认的未捕获异常处理程序来兜底

- 以 execute 方法提交到线程池的异步任务，最好在任务内部做好异常处理
- 设置自定义的异常处理程序作为保底，比如在声明线程池时自定义线程池的未捕获异常处理程序：

  ```java
  new ThreadFactoryBuilder().setNameFormat(prefix+"%d")
    .setUncaughtExceptionHandler((thread, throwable)-> log.error("ThreadPool {} got exception", thread, throwable))
    .get();
  ```
- 或者设置全局的默认未捕获异常处理程序：

  ```java
  static {
      Thread.setDefaultUncaughtExceptionHandler((thread, throwable)-> log.error("Thread {} got exception", thread, throwable));
  }
  ```

#### submit 方式

- 通过 submit 提交意味着我们关心任务的执行结果，应该通过拿到的 Future 调用其 get 方法来获得任务运行结果和可能出现的异常，否则异常可能就被生吞了

- 查看 FutureTask 源码可以发现，在执行任务出现异常之后，异常存到了一个 outcome 字段中，只有在调用 get 方法获取 FutureTask
  结果的时候，才会以 ExecutionException 的形式重新抛出异常：

```java
public void run() {
  ...
    try {
        Callable<V> c = callable;
        if (c != null && state == NEW) {
            V result;
            boolean ran;
            try {
                result = c.call();
                ran = true;
            } catch (Throwable ex) {
                result = null;
                ran = false;
                setException(ex);
            }
	...
        }

        protected void setException (Throwable t){
            if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
                outcome = t;
                UNSAFE.putOrderedInt(this, stateOffset, EXCEPTIONAL); // final state
                finishCompletion();
            }
        }

        public V get () throws InterruptedException, ExecutionException {
            int s = state;
            if (s <= COMPLETING)
                s = awaitDone(false, 0L);
            return report(s);
        }

        private V report ( int s) throws ExecutionException {
            Object x = outcome;
            if (s == NORMAL)
                return (V) x;
            if (s >= CANCELLED)
                throw new CancellationException();
            throw new ExecutionException((Throwable) x);
        }
```

### 问题

#### 关于在 finally 代码块中抛出异常的坑，如果在 finally 代码块中返回值，你觉得程序会以 try 或 catch 中的返回值为准，还是以 finally 中的返回值为准呢？

- 以 finally 中的返回值为准。
- 从语义上来说，finally 是做方法收尾资源释放处理的，我们不建议在 finally 中有 return，这样逻辑会很混乱。这是因为，实现上
  finally 中的代码块会被复制多份，分别放到 try 和 catch 调用 return 和 throw 异常之前，所以 finally 中如果有返回值，会覆盖
  try 中的返回值。

#### 对于手动抛出的异常，不建议直接使用 Exception 或 RuntimeException，通常建议复用 JDK 中的一些标准异常，比如IllegalArgumentException、IllegalStateException、UnsupportedOperationException。你能说说它们的适用场景，并列出更多常见的可重用标准异常吗？

- IllegalArgumentException：参数不合法异常，适用于传入的参数不符合方法要求的场景。
- IllegalStateException：状态不合法异常，适用于状态机的状态的无效转换，当前逻辑的执行状态不适合进行相应操作等场景。
- UnsupportedOperationException：操作不支持异常，适用于某个操作在实现或环境下不支持的场景。
- 还可以重用的异常有 IndexOutOfBoundsException、NullPointerException、ConcurrentModificationException 等。