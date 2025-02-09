## 如何实现线程唯一的单例？

> 参考代码：[IdGeneratorForClusterUnique.java](IdGeneratorForClusterUnique.java)

## 如何实现集群环境下的单例？

> - “进程唯一”指的是进程内唯一、进程间不唯一。
> - “线程唯一”指的是线程内唯一、线程间不唯一。
> - 集群相当于多个进程构成的一个集合，“集群唯一”就相当于是进程内唯一、进程间也唯一。

经典的单例模式是进程内唯一的

集群环境下的单例模式伪代码

```java
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private AtomicLong id = new AtomicLong(0);
    private static IdGenerator instance;
    private static SharedObjectStorage storage = FileSharedObjectStorage(/*入参省略，比如文件地址*/);
    private static DistributedLock lock = new DistributedLock();

    private IdGenerator() {
    }

    public synchronized static IdGenerator getInstance() {
        if (instance == null) {
            lock.lock();
            instance = storage.load(IdGenerator.class);
        }
        return instance;
    }

    public synchronized void freeInstance() {
        storage.save(this, IdGeneator.class);
        // 释放对象
        instance = null; 
        lock.unlock();
    }

    public long getId() {
        return id.incrementAndGet();
    }

    public static void main(String[] args) {
        // IdGenerator使用举例
        IdGenerator idGenerator = IdGenerator.getInstance();
        long id = idGenerator.getId();
        idGenerator.freeInstance();
    }
}
```

- 具体来说，需要把这个单例对象序列化并存储到外部共享存储区（比如文件）。
- 进程在使用这个单例对象的时候，需要先从外部共享存储区中将它读取到内存，并反序列化成对象。然后再使用，使用完成之后还需要再存储回外部共享存储区。
- 为了保证任何时刻，在进程间都只有一份对象存在，一个进程在获取到对象之后，需要对对象加锁，避免其他进程再将其获取。
- 在进程使用完这个对象之后，还需要显式地将对象从内存中删除，并且释放对对象的加锁。
