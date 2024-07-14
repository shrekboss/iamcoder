package org.coder.concurrency.programming.juc.atomic;

public class HelloExample {
	public static void main(String[] args) {
		HelloJNI jni = new HelloJNI();
		jni.sayHello("alex");
	}
}