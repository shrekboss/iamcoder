## Balking 设计模式

> Balking 设计模式在日常的开发中很常见，比如在系统资源的加载或者某些数据的初始化时，在整个系统中声明周期中资源可能只被加载一次，我们就可以采用
> Balking 设计模式加以解决。

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