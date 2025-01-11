## 如何实现一个支持给不同大小文件排序的小程序？

### 代码实现与分析

最简单直接的方式：

```java
public class Sorter {
    private static final long GB = 1000 * 1000 * 1000;

    public void sortFile(String filePath) {
        // 省略校验逻辑
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize < 6 * GB) { // [0, 6GB)
            quickSort(filePath);
        } else if (fileSize < 10 * GB) { // [6GB, 10GB)
            externalSort(filePath);
        } else if (fileSize < 100 * GB) { // [10GB, 100GB)
            concurrentExternalSort(filePath);
        } else { // [100GB, ~)
            mapreduceSort(filePath);
        }
    }

    private void quickSort(String filePath) {
        // 快速排序
    }

    private void externalSort(String filePath) {
        // 外部排序
    }

    private void concurrentExternalSort(String filePath) {
        // 多线程外部排序
    }

    private void mapreduceSort(String filePath) {
        // 利用MapReduce多机排序
    }
}

public class SortingTool {
    public static void main(String[] args) {
        Sorter sorter = new Sorter();
        sorter.sortFile(args[0]);
    }
}
```

### 代码优化与重构

参考代码如下：

- [ISortAlg.java](ISortAlg.java)
- [QuickSort.java](QuickSort.java)
- [ExternalSort.java](ExternalSort.java)
- [ConcurrentExternalSort.java](ConcurrentExternalSort.java)
- [MapReduceSort.java](MapReduceSort.java)
- [Sorter.java](Sorter.java)

消除 Sorter.java 中的 if-else，参考代码如下(查表法)：

- [Sorter1.java](Sorter1.java)

对于 Java 语言来说，可以通过反射来避免对策略工厂类的修改。具体是这么做的：

- 通过一个配置文件或者自定义的 annotation 来标注都有哪些策略类；
- 策略工厂类读取配置文件或者搜索被 annotation 标注的策略类，然后通过反射动态地加载这些策略类、创建策略对象；
- 当新添加一个策略的时候，只需要将这个新添加的策略类添加到配置文件或者用 annotation 标注即可。

对于 Sorter 来说，可以通过同样的方法来避免修改。通过将文件大小区间和算法之间的对应关系放到配置文件中。当添加新的排序算法时，只需要改动配置文件即可，不需要改动代码。
