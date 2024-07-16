package org.coder.concurrency.programming.juc._4_colleciton;

/**
 * 4.5 写时拷贝算法(Copy On Write)
 * 本节将学习另外一种并发容器————CopyOnWrite容器，简称COW，该容器的基本实现思路是在程序运行的初期，所有的线程都共享一个数据集合的引用。
 * 所有线程对该容器的读取操作将不会对数据集合产生加锁的动作，从而使得高并发吞吐量的读取操作变得高效，但是当有线程对该容器中的数据集合进行删除或增加等写操作时才会对整个数据集合进行加锁操作，
 * 然后将容器中的数据集合复制一份，并且基于最新的复制进行删除或增加等写操作，当写操作执行结束以后，将最新复制的数据集合引用指向原有的数据集合，进而达到读写分离最终一致性的目的。
 * <p>
 * 我们在前面也提到过这样做的好处是多线程对CopyOnWrite容器进行并发的读是不需要加锁的，因为当前容器中的数据集合是不会被添加任何元素的(关于这一点，CopyOnWrite算法可以保证)，
 * 所以CopyOnWrite容器是一种读写分离的思想，读和写不同的容器，因此不会存在读写冲突，而写写之间的冲突则是由全局的显式锁Lock来进行防护的，
 * 因此CopyOnWrite常常被用于读操作远远高于写操作的应用场景中。CopyOnWrite算法的基本原理如图4-20所示。
 * <p>
 * Java中提供了两种CopyOnWrite算法的实现类，具体如下，由于使用同样比较简单，在这里我们将不做过多讲述。
 * 1.CopyOnWriteArrayList:在JDK1.5版本被引入，用于高并发的ArrayList解决方案，在某种程度上可以替代Collections.synchronizedList。
 * 2.CopyOnWriteArraySet:也是自JDK1.5版本被引入，提供了高并发的Set的解决方案，其实在底层，CopyOnWriteArraySet完全是基于CopyOnWriteArrayList实现。
 * <p>
 * 无论是COW容器本身的用法还是内部实现CopyOnWrite都比较简单的，下面大致了解一下其内部的实现方式(以CopyOnWriteArrayList来看)。
 * <p>
 * 4.5.1 CopyOnWrite读实现操作分析
 * ...省略
 * public class CopyOnWriteArrayList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
 * private static final long serialVerionUID = 1L;
 * //显式锁ReentrantLock，主要用于对整个数据集合进行加锁操作
 * final transient ReentrantLock lock = new ReentrantLock();
 * //数据集合，引用被volatile修饰，保证线程间的可见性
 * private transient volatile Object[] array;
 * //读取方法，调用另外一个get方法，并未加锁，支持高并发多线程同时读取
 * public E get(int index){
 * return get(getArray(), index);
 * }
 * <p>
 * private E get(Object[] a, int index){
 * return (E) a[index];
 * }
 * <p>
 * final Object[] getArray(){
 * return array;
 * }
 * }
 * ...省略
 * 在上面的CopyOnWrite源码中我们发现，在对CopyOnWrite容器进行读操作时不会进行加锁同步操作，因此允许同一时间多个线程同时操作。
 * <p>
 * 4.5.2 CopyOnWrite写实现操作分析
 * CopyOnWrite容器在进行写操作时，首先会加锁整个容器，然后拷贝一份新的副本，再针对副本进行操作，最后将副本赋值于全局的数据集合引用，
 * 由于锁的加持，写操作在同一时刻只允许一个线程进行写操作，下面同样以CopyOnWriteArrayList为例简单分析一下。
 * ...省略
 * public boolean add (E e) {
 * final ReentrantLock lock = this.lock;
 * //加锁
 * lock.lock();
 * try{
 * Object[] elements = getArray();
 * int len = elements.length;
 * //拷贝数据集合(数组)
 * Object[] newElements = Arrays.copyOf(elements, len + 1);
 * //新增数据
 * newElements[len] = e;
 * //更新COW容器中数据集合引用指向新的数据集合
 * setArray(newElements);
 * return true;
 * }finally{
 * //锁释放
 * lock.unlock();
 * }
 * }
 * ...省略
 * <p>
 * 4.5.3 CopyOnWrite总结
 * 虽然COW算法为解决高并发读操作提供了一种新的思路，但是其仍然存在一些天生的缺陷，具体如下。
 * 1.数组复制带来的内存开销：因为CopyOnWrite的写时复制机制，所以在进行写操作的时候，内存里会同时驻扎两个对象的内存，旧的数据集合和新拷贝的数据集合，
 * 当然旧的数据集合在拷贝结束以后会满足被回收的条件，但是在某个时间段内，内存还是会有将近一半的浪费。
 * 2.CopyOnWrite并不能保证实时的数据一致性：CopyOnWrite容器只能保证数据的最终一致性，并不能保证数据的实时一致性。
 * 举个例子，假设A线程修改了数据复制并且增加了一个新的元素但是未将数据集合的引用指向最新复制，与此同时，
 * B线程是从旧的数据集合中读取元素，因此A写入的数据并不能实时地被B线程读取。
 * <p>
 * 既然CopyOnWrite并不是一个很完美的高并发线程安全解决方案，那么它的应用场景又该是怎样的呢？
 * 其实我们在本节中已经提到过了，对于读操作远大于写操作，并且不要求实时数据一致性的情况，CopyOnWrite容器将是一个很合理的选择，
 * 比如在规则引擎中对新规则的引入、在告警规则中对新规则的引入、在黑白名单中对新数据的引入，并不一定需要严格保证数据的实时一致性，
 * 我们只需要确保在单位时间后的最终一致性即可，在这种情况下，我们就可以采用COW算法提高数据的读取速度及性能。
 */
public class CopyOnWriteTest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}