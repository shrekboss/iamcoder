package org.coder.concurrency.programming.juc._4_colleciton;

/**
 * 4.4 ConcurrentMap(并发映射)
 * Java程序员在日常的开发中除了经常使用List、Queue、Set等数据集合以外，Map这样的数据结构也是使用最多的数据结构之一。
 * Map是一个接口，它的实现方式有很多种，比如常见的HashMap、LinkedHashMap，但是这些Map的实现并不是线程安全的，在多线程高并发的环境中会出现线程安全的问题。
 * Hashtable或者SynchronizedMap虽然是线程安全的，但是在多线程高并发的环境中，简单粗暴的排他式加锁方式效率并不是很高。
 * <p>
 * 鉴于Map是一个在高并发的应用环境中应用比较广泛的数据结构，Doug Lea自JDK1.5版本起在Java中引入了ConcurrentHashMap并且在随后的JDK版本迭代中都在不遗余力地为性能提升做出努力，
 * 除了ConcurrentHashMap之外，大师Doug Lea也在JDK1.6版本中引入了另外一个高并发Map的解决方案ConcurrentSkipListMap。
 * <p>
 * 这些新的Map在使用上与HashMap并没有很大的不同，但是其内部的实现却是非常复杂的，作为程序员真的应该感谢甚至感恩Doug Lea大师为其付出的心血和努力，
 * 才使得我们不用面对底层复杂的数据结构实现，复杂的多线程场景下的性能细节等。
 * <p>
 * 4.4.1 ConcurrentHashMap简介
 * ConcurrentHashMap的内部实现几乎在每次JDK版本升级的过程中都会随之升级优化，本节只是简单分析一下ConcurrentHashMap之所以如此高效的原理即可，
 * 因为其中所涉及的数据结构超出了本书的范围，关于更深层次的内容，读者可以参阅其他资料。总体来讲，ConcurrentHashMap是专门为多线程高并发场景而设计的Map，
 * 它的get()操作基本上是lock-free的，同时put()方法又将锁的粒度控制在很小的范围之内，因此它非常适合于多线程的应用程序之中。
 * <p>
 * 1.JDK1.8版本以前的ConcurrentHashMap内部结构
 * 在JDK1.6、1.7版本中，ConcurrentHashMap采用的是分段锁的机制(可以在确保线程安全的同时最小化锁的粒度)实现并发的更新操作，
 * 在ConcurrentHashMap中包含两个核心的静态内部类Segment和HashEntry，前者是一个实现自ReentrantLock的显式锁，
 * 每一个Segment锁对象均可用于同步每个散列映射表的若干个桶(HashBucket)，后者主要用于存储映射表的键值对。与此同时，
 * 若干个HashEntry通过链表结构形成了HashBucket，而最终的ConcurrentHashMap则是有若干个(默认是16个)Segment对象数组构成的，如图4-18所示。
 * <p>
 * Segment可用于实现减小锁的粒度，ConcurrentHashMap被分割成若干个Segment，在put的时候只需要锁住一个Segment即可，
 * 而get时候则干脆不加锁，而是使用volatile属性以保证被其他线程同时修改后的可见性。
 * <p>
 * 2.JDK1.8版本ConcurrentHashMap的内部结构
 * 在JDK1.8版本中几乎重构了ConcurrentHashMap的内部实现，摒弃了segment的实现方式，直接用table数组存储键值对，
 * 在JDK1.6中，每个bucket中键值对的组织方式都是单向链表，查找复杂度是O(n)，
 * JDK1.8中当链表长度超过8时，链表转换为红黑树，查找复杂度可以降低到O(log n)，改进了性能。
 * 利用CAS+synchronized可以保证并发更新的安全性，底层则采用数组+链表+红黑树(提高检索效率)的存储结构，如图4-19所示。
 * <p>
 * 4.4.2 ConcurrentSkipListMap简介
 * ConcurrentSkipListMap提供了一种线程安全的并发访问的排序映射表。内部是SkipList结构实现，在理论上，其能够在O(log(n))时间内完成查找、插入、删除操作。
 * 调用ConcurrentSkipListMap的size时，由于多个线程可以同时对映射表进行操作，所以映射表需要遍历整个链表才能返回元素的个数，这个操作是个O(log(n))的操作。
 * <p>
 * 在读取性能上，虽然ConcurrentSkipListMap不能与ConcurrentHashMap相提并论，但是ConcurrentSkipListMap存在着如下两大天生的优越性是ConcurrentHashMap所不具备的。
 * 第一，由于基于跳表的数据结构，因此ConcurrentSkipListMap的可以是有序的。
 * 第二，ConcurrentSkipListMap支持更高的并发，ConcurrentSkipListMap的存取时间复杂度是O(log(n))，与线程数几乎无关，
 * 也就是说，在数据量一定的情况下，并发的线程越多，ConcurrentSkipListMap越能体现出它的优势。
 * <p>
 * 4.4.3 并发映射总结
 * 关于ConcurrentMap就介绍这么多，至于如何使用这些高并发的Map，相信对读者而言并不是一件复杂的事情，但是如果要细致地剖析其内部的结构实现和方法实现也不是一件容易的事情，
 * 想要深入地了解和掌握ConcurrentMap，尤其是ConcurrentHashMap，必须得具备很扎实的数据结构基础，比如了解链表、跳表、平衡树、红黑树
 * (当然熟练平衡树的前提是需要具备二叉树、二叉搜索树等相关的知识)，与此同时还需要Lock-Free、CAS等相关的知识(关于这些知识点，本书的第2章中做了很详细的讲解)。
 */
public class ConcurrentMapTest {

}