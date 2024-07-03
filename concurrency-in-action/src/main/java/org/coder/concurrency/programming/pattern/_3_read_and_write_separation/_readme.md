## 读写分离程序设计模式

> 使用场景：读多写少时，可以使用读写锁。如果写线程的数量和读线程的数量接近甚至多于读线程情况下，因此在 JDK 1.8 中又增加了
> StampedLock 的解决方案。

参考代码：

- [Lock.java](Lock.java)
    - [ReadLock.java](ReadLock.java)
    - [WriteLock.java](WriteLock.java)
- [ReadWriteLock.java](ReadWriteLock.java)
    - [ReadWriteLockImpl.java](ReadWriteLockImpl.java)
- [ShareData.java](ShareData.java)
- [ReadWriteLockTest.java](ReadWriteLockTest.java)

ReadWriteLock 虽然名字中有 lock，但是它并不是 lock，它主要是用于创建 read lock 和 write lock 的，并且提供了查询功能用于查询当前有多少个
reader 和 writer 以及 waiting 中的 writer。

ReadWriteLockImpl 更像是一个工厂类，可以通过它创建不同类型的锁。

- 包可见：透明其实现细节
    - ReadWriteLockImpl
    - ReadLock
    - WriteLock
