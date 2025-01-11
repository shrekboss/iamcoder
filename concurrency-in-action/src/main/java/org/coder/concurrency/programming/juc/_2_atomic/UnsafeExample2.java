package org.coder.concurrency.programming.juc._2_atomic;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 2.直接修改内存数据
 * 我们来看下面这样的一段程序代码。
 * <p>
 * 非常简单，是吧？没错！Guard提供了一个方法canAccess()用于校验传入的数值是否与accessNo相等，如果不相等则我们会拒绝某些事情的发生，
 * 通常情况下，为了使得canAccess()返回true，我们只需要传入与accessNo相等的数值即可，但是Unsafe可以直接修改accessNo在内存中的值。
 */
public class UnsafeExample2 {

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("can't initial the unsafe instance.", e);
        }
    }

    public static void main(String[] args) throws NoSuchFieldException, SecurityException {
        Guard guard = new Guard();
        assert !guard.canAccess(10);
        assert guard.canAccess(1);

        Unsafe unsafe = getUnsafe();
        //获取accessNo
        Field f = guard.getClass().getDeclaredField("accessNo");
        //使用unsafe首先获得f的内存偏移量
        //然后直接进行内存操作，将accessNo的值修改为20
        unsafe.putInt(guard, unsafe.objectFieldOffset(f), 20);
        //断言成功
        assert guard.canAccess(20);
    }

    static class Guard {
        private int accessNo = 1;

        public boolean canAccess(int no) {
            return this.accessNo == no;
        }
    }
}