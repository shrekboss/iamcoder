package org.coder.concurrency.programming.juc._2_atomic;

public class HelloExample {
	public static void main(String[] args) {
		HelloJNI jni = new HelloJNI();
		jni.sayHello("alex");
	}
}