## Future 设计模式

> 也叫做 Promise 模式。这是一种异步编程模式(也支持同步方式的编程)。
> 它使得我们可以开始一个任务的执行，并得到一个用于获取该任务执行结果的凭据对象，而不必等待该任务执行完毕就可以继续执行其他操作。等到我们需要该任务的执行结果时，在调用凭据对象的相关方法来获取。

- 可以增强点：
    - 将提交的任务交给线程池运行。
    - get 方法没有超时功能，如果获取一个计算结果在规定的时间内没有返回，则可以抛出异常通知调用线程。
    - Future 未提供 Cancel 功能，当任务提交之后还可以对其进行取消。
    - 任务运行时出错未提供回调方式。

参考代码：

- [Future.java](Future.java)
    - [FutureTask.java](FutureTask.java)
- [FutureService.java](FutureService.java)
    - [FutureServiceImpl.java](FutureServiceImpl.java)
- [Callback.java](Callback.java)
- [Task.java](Task.java)
- [FutureTest.java](FutureTest.java)

### 模式实战案例解析

参考代码：

- [DataSyncTask.java](ftp%2FDataSyncTask.java)
- [FTPClientUtil.java](ftp%2FFTPClientUtil.java)
- [FTPUploaderhaoh.java](ftp%2FFTPUploader.java)
- [FTPUploaderPromisor.java](ftp%2FFTPUploaderPromisor.java)

### 模式的评价与实现考量

promise 模式既发挥了异步编程的优势 --- 增加系统的并发性，减少不必要的等待，又保持了同步编程的简单性：有关异步编程的细节，如创建新的线程或者提交任务到线程池等细节，都被封装在
Promisor 参与者实例中， 而 Promise 的客户端代码则无需关心这些细节，其编码方式与同步编程并无本质上的去差别。

Promise 模式的异步编程这一特性：为了减少客户端代码在调用 Promise 的 getResult 方法时出现阻塞的可能，客户端代码应该尽可能早地调用
Promisor 的异步方法，并尽可能地调用晚地调用 Promise 的 getResult 方法。这当中间隔的时间可以由客户端代码来执行其他操作，同时这段时间可以get
TaskExecutor 用于执行异步任务。

FTPClientUtil 的 newInstance 方法如果改成同步方法，我们只需要将其方法体中的语句 new Thread(task).start() 改为 task.run()
即可。这在一定程度上屏蔽嘞同步、异步编程的差异。而这可以给代码调式或者问题定位带来一定的便利。

### 异步方法的异常处理

如果 Promise 的 computer 方法是个异步方法，那么客户端代码在调用完该方法后异步任务可能尚未开始执行。另外，异步任务运行在自己的线程中，而不是
compute 方法的调用线程中。因此，异步任务执行过程中产生的异常无法在 compute 方法中抛出。为了让 Promise
模式的客户端代码能够捕获到异步任务执行过程中出现的异常，一个可行的办法是让 TaskExecutor 在执行任务捕获到异常后，将异常对象“记录”到
Promise 实例的一个专门的实力变量上，然后由 Promise 实例的 getResult 方法对该实例变量进行检查。若该实例变量的值不为 null，则
getResult 方法抛出异常。JDK 中提供的类 java.util.concurrent.FutureTask 就是采用这种方法对 compute 异步方法的异常进行处理的。

`FutureTask实例.setException(XxxException)`

### 轮训(polling)

Promise 需要暴露一个 isDone 方法用于检测异步任务是否已执行完毕。JDK 提供的类 java.util.concurrent.FutureTask 的 isDone
方法正是处于这种考虑，它允许我们在“适当”的时候才调用 Promise 的 getResult 方法(相当于 FutureTask 的 get 方法)。

### 异步任务的执行

如果系统同时存在过个线程调用 Promisor 的异步方法，而每个异步方法都启动了各自的线程去执行异步任务，这可能导致一个 JVM
中启动的线程数量过多，增加了线程调度的负担，从而反倒降低了系统的性能。因此，异步任务改用线程池去执行。