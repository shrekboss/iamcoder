## Balking 设计模式

> 多线程监控某个共享变量，A 线程监控到共享变量发生变化后即将出发某个动作，但是此时发现有另外一个线程 B
> 已经针对改变的变量的变化开始了动作，因此 A 线程便放弃了准备开始的工作，把这样的线程间交互称为 Balking(犹豫) 设计模式。
>
> Balking 设计模式在日常的开发中很常见，比如在系统资源的加载或者某些数据的初始化时，在整个系统中声明周期中资源可能只被加载一次，我们就可以采用
> Balking 设计模式加以解决。

参考代码：

- [Document.java](Document.java)
- [AutoSaveThread.java](AutoSaveThread.java)
- [DocumentEditThread.java](DocumentEditThread.java)
- [BalkingTest.java](BalkingTest.java)

模板代码：

```java
public synchronized Map<String, Resource> load() {
    // balking
    if (loaded) {
        return resourceMap;
    } else {
        // do the resource load task
        // ...
        this.loaded = true;
        return resourceMap;
    }
}
```