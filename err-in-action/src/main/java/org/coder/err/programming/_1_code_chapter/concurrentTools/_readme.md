## 使用了并发工具类库，线程安全就高枕无忧了吗？

1. 没有意识到线程重用导致用户信息错乱的Bug

> ThreadLocal 适用于变量在线程间隔离，而在方法或类间共享的场景。

使用 ThreadLocal 存储用户信息，按理说，在设置用户信息之前第一次获取的值始终应该是 null，但我们要意识到，程序运行在 Tomcat
中，执行程序的线程是 Tomcat 的工作线程，而 Tomcat 的工作线程是基于线程池的。

顾名思义，线程池会重用固定的几个线程，一旦线程重用，那么很可能首次从 ThreadLocal 获取的值是之前其他用户的请求遗留的值。这时，ThreadLocal
中的用户信息就是其他用户的信息。

- 在写业务代码时，首先要理解代码会跑在什么线程上：
    - 我们可能会抱怨学多线程没用，因为代码里没有开启使用多线程。但其实，可能只是我们没有意识到，在 Tomcat 这种 Web
      服务器下跑的业务代码，本来就运行在一个多线程环境（否则接口也不可能支持这么高的并发），并不能认为没有显式开启多线程就不会有线程安全问题。
    - 因为线程的创建比较昂贵，所以 Web 服务器往往会使用线程池来处理请求，这就意味着线程会被重用。这时，使用类似 ThreadLocal
      工具来存放一些数据时，需要特别注意在代码运行完后，显式地去清空设置的数据。如果在代码中使用了自定义的线程池，也同样会遇到这个问题。


- 参考代码：[threadlocal](threadlocal)

2. 使用了线程安全的并发工具，并不代表解决了所有线程安全问题

> ConcurrentHashMap 只能保证提供的原子性读写操作是线程安全的。

案例场景说明：有一个含 900 个元素的 Map，现在再补充 100 个元素进去，这个补充操作由 10 个线程并发进行。

ConcurrentHashMap 对外提供的方法或能力的限制：

- 使用了 ConcurrentHashMap，不代表对它的多个操作之间的状态是一致的，是没有其他线程在操作它的，如果需要确保需要手动加锁。
    - 诸如 size、isEmpty 和 containsValue 等聚合方法，在并发情况下可能会反映 ConcurrentHashMap
      的中间状态。因此在并发情况下，这些方法的返回值只能用作参考，而不能用于流程控制。显然，利用 size 方法计算差异值，是一个流程控制。
    - 诸如 putAll 这样的聚合方法也不能确保原子性，在 putAll 的过程中去获取数据可能会获取到部分数据。

- 参考代码：[concurrenthashmapmisuse](concurrenthashmapmisuse)

3. 没有充分了解并发工具的特性，从而无法发挥其威力

computeIfAbsent 为什么如此高效呢？

- Java 自带的 Unsafe 实现的 CAS。它在虚拟机层面确保了写入数据的原子性，比加锁的效率高得多
    ```java
    
    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i,
                                         Node<K, V> c, Node<K, V> v) {
        return U.compareAndSetObject(tab, ((long) i << ASHIFT) + ABASE, c, v);
    }
    ```

ConcurrentHashMap 中 computeIfAbsent 死循环bug问题

- http://www.111com.net/jsp/189310.htm

案例场景说明：使用 Map 来统计 Key 出现次数的场景吧，这个逻辑在业务代码中非常常见

- 使用 ConcurrentHashMap 来统计，Key 的范围是 10。
- 使用最多 10 个并发，循环操作 1000 万次，每次操作累加随机的 Key。
- 如果 Key 不存在的话，首次设置值为 1。

- 巧妙利用了下面两点：
    - 使用 ConcurrentHashMap 的原子性方法 computeIfAbsent 来做复合逻辑操作，判断 Key 是否存在 Value，如果不存在则把 Lambda
      表达式运行后的结果放入 Map 作为 Value，也就是新创建一个 LongAdder 对象，最后返回 Value。
    - 由于 computeIfAbsent 方法返回的 Value 是 LongAdder，是一个线程安全的累加器，因此可以直接调用其 increment 方法进行累加。

ThreadLocalRandom，是否可以把它的实例设置到静态变量中，在多线程情况下重用呢?

- ThreadLocalRandom#current() 的时候初始化一个初始化种子到线程，每次 nextseed
  再使用之前的种子生成新的种子：UNSAFE.putLong(t = Thread.currentThread(), SEED, r = UNSAFE.getLong(t, SEED) + GAMMA);
- 如果通过主线程调用一次 current 生成一个 ThreadLocalRandom 的实例保存起来，那么其它线程来获取种子的时候必然取不到初始种子，必须是每一个线程自己用的时候初始化一个种子到线程。
  可以在 nextSeed 方法设置一个断点来测试：UNSAFE.getLong(Thread.currentThread(),SEED);

- 参考代码：[concurrenthashmapperformance](concurrenthashmapperformance)

4. 没有认清并发工具的使用场景，因而导致性能问题

> 在 Java 中，CopyOnWriteArrayList 虽然是一个线程安全的
> ArrayList，但因为其实现方式是，每次修改数据时都会复制一份数据出来，所以有明显的适用场景，即读多写少或者说希望无锁读的场景。

- 参考代码：[copyonwritelistmisuse](copyonwritelistmisuse)

5. putIfAbsent vs computeIfAbsent的一些特性比对

- computeIfAbsent 和 putIfAbsent 方法的区别？
    - 当 Key 存在的时候，如果 Value 的获取比较昂贵的话，putIfAbsent 方法就会白白浪费时间在获取这个昂贵的 Value
      上（这个点特别注意），而
      computeIfAbsent 则会因为传入的是 Lambda 表达式而不是实际值不会有这个问题。
    - Key 不存在的时候，putIfAbsent 会返回 null，这时候要小心空指针；而 computeIfAbsent 会返回计算后的值，不存在空指针的问题。
    - 当 Key 不存在的时候，putIfAbsent 允许 put null 进去，而 computeIfAbsent 不能（当然了，此条针对
      HashMap，ConcurrentHashMap
      不允许 put null value 进去）。

- 参考代码：[ciavspia](ciavspia)

6. 异步执行多个子任务等待所有任务结果汇总处理的例子

- 参考代码：[multiasynctasks](multiasynctasks)