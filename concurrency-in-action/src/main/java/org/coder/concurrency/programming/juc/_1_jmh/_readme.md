# Java Microbenchmark Harness(JMH)

[JMH Demo](https://hg.openjdk.org/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/)

[JMH 结果可视化工具](https://jmh.morethan.io/)

## Benchmark

* `@Benchmark`: 标记基准测试方法 [`ElementType.METHOD`]

JMH 对基准测试的方法使用 `@Benchmark` ([JMHExample01.java](JMHExample01.java))注解进行标记，否则方法将被视作普通方法，并不会对其执行基准测试。

对一个类如果没有任何基准测试方法(被 `@Benchmark` 标记的方法)
，进行基准测试会出现异常([JMHExample02.java](JMHExample02.java))。

通常每个 `@Benchmark` 注解的方法都运行在独立的进程中，互不干涉。

## Warmup and Measurement

1. 全局 `Options` 接口配置，拥有最高优先级: `ChainedOptionsBuilder warmupIterations(int value)`
   `ChainedOptionsBuilder measurementIterations(int count)`

2. 注解方式
    * `@Warmup`: 代码预热: [`ElementType.METHOD,ElementType.TYPE`]

      在基准测试代码正式度量之前进行预热，使得代码经理类的早期优化、JVM 运行期编译、JIT 优化之后的状态，从而能够获得代码真实的性能数据。

        * `iterations` 预热的次数
        * `time` 每次预热的时间
        * `timeUnit` 时间单位(默认`s`)
        * `batchSize` 批处理大小，每次操作调用几次方法

    * `@Measurement`: 代码度量: [`ElementType.METHOD,ElementType.TYPE`]

      与 `ChainedOptionsBuilder measurementIterations(int count)` 功能相同

        * `iterations` 预热的次数
        * `time` 每次预热的时间
        * `timeUnit` 时间单位(默认`s`)
        * `batchSize` 批处理大小，每次操作调用几次方法

      预热数据不会在纳入统计之中，只有度量数据纳入统计之中。

> 优先级： `Options` 配置 > 方法注解 > 类注解

## 测试模式

### 注解模式 `@BenchmarkMode`

`@BenchmarkMode`(`ElementType.METHOD, ElementType.TYPE`) 声明使用的测试模式：

* `Mode.AverageTime` 平均响应时间 主要用于输出基准测试方法调用灭一次所耗费的时间 elapsed time/operation
* `Mode.Throughput` 方法吞吐量 主要用于输出基准测试方法在单位时间内可以对该方法调用多少次
* `Mode.SampleTime` 时间采样 主要用于输出基准测试方法执行时间区间分布
* `Mode.SingleShotTime` 单次操作时间 主要用来进行冷测试，不论是 `Warmup` 还是 `Measurement` ，在每一个批次中基准测试方法只会被执行一次，一般情况下将
  `Warmup` 设置为 `0`
* `Mode.All` 对如上所有模式进行测试

### 全局配置

`Options` 接口配置方式，拥有最高优先级： `ChainedOptionsBuilder mode(Mode mode)`

## 输出单位

* `@OutputTimeUnit()` 注解方式
* `ChainedOptionsBuilder timeUnit(TimeUnit tu)` 使用 `Options` 配置

## `State`

定义对象在工作线程之间共享的程度

* `Scope.Thread`: 线程独享

  每一个运行基准方法的线程都会持有一个独立的对象实例，该实例既可能是作为基准测试方法参数传入的，也可能是运行基准测试方法所在的宿主
  class 。

  `State` 设置为 `Scope.Thread` 主要是针对非线程安全类。

* `Scope.Thread`<**默认**> 每个线程分配一个独立的对象实例：主要针对非线程安全的类
* `Scope.Benchmark`  所有测试线程共享一个实例，测试有状态实例在多线程共享下的性能，测试在多线程的情况下，某个类被不同线程操作时的性能
* `Scope.Group` 作用域为 Group ，同一个线程在同一个 Group 里共享实例

## Param

`@Param` 注解使的参数可配置，即每一次的基准测试时都有不同的值与之对应。

## JMH 测试套件 Fixture

* `@Setup` 基准测试之前调用
* `@TearDown` 基准测试之后调用

默认情况下， `Setup` `TearDown` 会在一个基准方法的所有批次执行前后分别执行；
如果需要在每一个批次或者每一次基准方法调用执行的前后执行对应的套件方法，则需要对 `@Setup` `@TearDown` 进行简单的配置：

* `Level`: 控制何时运行夹具
    * `Trial`: **默认**在每一个基准测试方法的所有批次执行的前后被执行；
    * `Iteration`: 在每一个基准测试批次执行的前后调用套件方法；
    * `Invocation`: 在每一个批次的度量过程中，每一次对基准方法的调用前后都会执行套件方法；**此级别仅适用于每次基准测试方法调用花费超过一毫秒的基准测试；
      **
        1. 警告：基准测试时间必须减去夹具的开销成本，因这次在级别上，必须为**每个**
           基准调用添加时间戳；如果基准测试方法很小，那么请求时间戳会使得系统饱和，引入人为的延迟、吞吐量和可扩展性瓶颈；
        2. 警告：测量级别为单个调用的时间，我们可能为自己设置（协调）遗漏；这意味着测量中的小问题可能从时许测量中来，并且可能带来令人惊讶的结果。例如，当我们使用时序来了解基准吞吐量时，省略时序测量将会导致聚合时间减少，并虚构
           **更大**吞吐量
        3. 警告：为了保持与其他级别相同的共享行为，我们有时必须同步（`arbitrage`）对 `State`
           对象的访问。其他级别在测量之外执行此操作，但在这个级别，我们必须在**关键路径**上同步，进一步抵消测量。
        4. 警告：当前实现允许此级别的辅助方法执行与基准测试本身重叠，以简化套利。这在多线程基准测试中很重要，当一个工作线程执行
           Benchmark 方法时可能同时观察到其他工作线程已经为同一个对象调用 `TearDown` 。

## CompilerControl 编译控制

`@CompilerControl`: 控制编译器行为

* `CompilerControl.Mode`: 编译模式(此类适用于任何类/方法，即使是那些未被其他 JMH 注解表姐的类或方法)
    * `BREAK`: 将断点插入到生成的已编译代码中
    * `PRINT`: 打印方法与它的 profile
    * `EXCLUDE`: 将该方法从编译中排除
    * `INLINE`: 强制内联
    * `DONT_INLINE`: 强制跳过内联
    * `COMPILE_ONLY`: 只编译这个方法，而不编译其他的

> 其他禁止编译器优化方法：
>
> * 程序中禁止 JVM 运行期编译和优化： `java.lang.Compiler.disable();`
> * 启动 JVM 是参数控制： `-Djava.compile=NONE`

## 正确编写微基准测试

避免 Java 虚拟机 JVM 在早期编译阶段、加载阶段以及后期的运行时对代码进行的相关优化（比如： Dead Code 的擦除、常量的折叠、循环代开以及进程
Profiler 优化等）。

### 1. 避免 DCE(Dead Code Elimination)

Dead Code Elimination 是指 JVM 擦去了一些上下文无关，甚至经过计算之后确定压根不会用到的代码。

**若想编写良好的微基准测试方法，则不要让方法存在 Dead Code ，最好每一个基准测试方法都有返回值。**

### 2. 使用 Blackhole

JVM 提供了一个称为 `Blackhole` 的类，可以在不作任何返回的情况下避免 Dead Code 的发生， `Blackhole` 直译为**黑洞**，与 Linux
系统下的黑洞设备 `/dev/null` 类似。

`Blackhole#consume()`

### 3. 避免常量折叠(Constant Folding)

常量折叠是 Java 编译器早期的一种优化——**编译优化**。
在 `javac` 对源文件进行编译的过程中，通过词法分析可以发现某些常量是可以被折叠的，即可以直接将计算结果存放到声明中，而不需要在执行阶段再次进行运算。

### 4. 避免循环展开(Loop Unwinding)

循环代码在运行阶段( JVM 后期优化)极有可能进行循环展开优化，即将循环体增长，循环次数降低。

```jshelllanguage
    int sum = 0;
    for (int i = 0; i < 100; i++) {
        sum += i;
    }
    // 循环展开
    int sum = 0;
    for (int i = 0; i < 20; i += 5) {
        sum += i;
        sum += i + 1;
        sum += i + 2;
        sum += i + 3;
        sum += i + 4;
    }
```

### 5. Fork 用于避免 Profile-guided optimizations

Java 支持多线程但是不支持多进程，这就导致所有的代码都在一个进程中运行，
相同的代码在不同时刻的执行可能会引入前一阶段对进程 profile 的优化，甚至会混入其他代码 profiler
优化时的参数，这可能会导致所编写的微基准代码出现不准确的我呢提。

`Fork`: 指定进程数量：一般情况下，只需要将 `Fork` 设置为 `1`

* `0`： 每一个基准测试方法都会与基准测试类共享同样的进程 Profiler
* `1`： 每一个基准测试方法开辟心的进程去运行
* `n`> 1: 基准测试将运行在不同的进程中。

## 高级用法

### 1. `Asymmetric Benchmark`

编写的所有基准测试都会被 JMH 框架根据方法名的字典顺序之后串行执行，有些时候希望对某个类的**读写方法并行执行**。

`@GroupThreads` 组线程定义有多少线程参与运行组中的特定基准方法。用于运行具体方法的线程数。

### 2. Interrupts Benchmark

针对某些容器的读写操作时可能会引起阻塞，阻塞并不是容器无法保证线程安全问题，而是由于 JMH 框架的机制引起的。

可以通过设置 `Options` 的 `timeout` 强制让每一个批次的度量超时，超时的基准测试数据将不会被纳入统计之中。

## JMH 的 Profiler

JMH 提供的 Profiler

| Profiler 名称 | Profiler 描述                                        |
|-------------|----------------------------------------------------|
| **`STACK`** | JVM 线程栈信息分析                                        |
| **`GC`**    | 通过 Standard MBean 进行 Benchmark 方法的 GC 分析           |
| **`CL`**    | 分析执行 Benchmark 方法时的类加载情况                           |
| **`COMP`**  | 通过 Standard MBean 进行 Benchmark 方法的 JIT 编译器分析       |
| `HS_CL`     | HotSpot™ 类加载器通过特定于实现的 MBean 进行分析                   |
| `HS_COMP`   | HotSpot™ JIT 通过特定于实现的 MBean 编译分析                   |
| `HS_GC`     | Hotspot™ 内存管理(GC)通过特定于实现的 MBean 进行分析               |
| `HS_RT`     | 通过 Implementation-Specific MBean 进行 HotSpot™ 运行时分析 |
| `HS_THR`    | 通过 Implementation-Specific MBean 进行 HotSpot™ 线程分析  |

- `ChainedOptionsBuilder addProfiler(Class<? extends Profiler> profiler)`
- `ChainedOptionsBuilder addProfiler(Class<? extends Profiler> profiler, String initLine)`
- `ChainedOptionsBuilder addProfiler(String profiler)`
- `ChainedOptionsBuilder addProfiler(String profiler, String initLine)`

### StackProfiler

StackProfiler 输出堆栈信息，统计程序在执行过程中线程的数据（线程状态信息）

`StackProfiler.class`

### GCProfiler

GcProfiler 用于分析在测试方法中垃圾回收器在 JVM 每个内存空间上所花费的时间

`GCProfiler.class`

example:

```text
Iteration   1: 0.968 us/op
                 ·gc.alloc.rate:      10130.486 MB/sec
                 ·gc.alloc.rate.norm: 10280.000 B/op
                 ·gc.count:           1364.000 counts
                 ·gc.time:            373.000 ms
```

### ClassLoaderProfiler

ClassLoaderProfiler 在基准测试过程中所有类的加载与卸载；
考虑到在一个类加载器中同一个类只会加载一次，因此需要将 `Warmup` 设置为 `0` ，避免预热阶段加载基准测试方法所需要的所有类。

`ClassloaderProfiler.class`

example:

```text
Iteration   1: 1.630 us/op
                 ·class.load:        661.429 classes/sec
                 ·class.load.norm:   ≈ 10⁻⁵ classes/op
                 ·class.unload:      ≈ 0 classes/sec
                 ·class.unload.norm: ≈ 0 classes/op
```

```text
Benchmark                                      Mode  Cnt    Score      Error        Units
JMHExample23.testLoadClass                     avgt    5    1.397 ±    0.502        us/op
JMHExample23.testLoadClass:·class.load         avgt    5  132.286 ± 1139.021  classes/sec
JMHExample23.testLoadClass:·class.load.norm    avgt    5   ≈ 10⁻⁶              classes/op
JMHExample23.testLoadClass:·class.unload       avgt    5      ≈ 0             classes/sec
JMHExample23.testLoadClass:·class.unload.norm  avgt    5      ≈ 0              classes/op
```

### CompilerProfiler

CompilerProfiler 在基准测试过程中 JIT编译器所花费的优化时间；打开 `verbose` 模式观察更详细的输出；

`CompilerProfiler.class`

example:

```text
Iteration   1: 1.158 us/op
                 ·compiler.time.profiled: 2.000 ms
                 ·compiler.time.total:    365.000 ms
```

```text
Benchmark                                           Mode  Cnt    Score   Error  Units
JMHExample24.testLoadClass                          avgt    5    1.169 ± 0.109  us/op
JMHExample24.testLoadClass:·compiler.time.profiled  avgt    5    7.000             ms
JMHExample24.testLoadClass:·compiler.time.total     avgt    5  370.000             ms
```
