package org.coder.concurrency.programming.juc._2_atomic;

/**
 * 2.8 sun.misc.Unsafe 详解
 * Java是一种安全的开发语言，Java的设计者在设计之初就想将一些危险的操作屏蔽掉。
 * 比如对内存的手动管理，但是本章所学习的原子类型，甚至在接下来的章节中将要学习到的并发工具、并发容器等在其底层都依赖于一个特殊的类sun.misc.Unsafe，
 * 该类是可以直接对内存进行相关操作的，甚至还可以通过汇编指令直接进行CPU的操作。
 * <p>
 * sun.misc.Unsafe提供了非常多的底层操作方法，这些方法更加接近机器硬件(CPU/内存)，因此效率会更高。不仅Java本身提供的很多API都对其有严重的依赖，
 * 而且很多优秀的第三方库/框架都对它有着严重的依赖，比如LMAX Disruptor，不熟悉系统底层，不熟悉C/C++汇编等的开发者没必要对它进行深究，但是这并不妨碍我们直接使用它。
 * 在使用的过程中，如果使用不得当，那么代价将是非常高昂的，因此该类被命名为Unsafe也就在情理之中了，总之一句话，你可以用，但请慎用！
 * <p>
 * 2.8.1 如何获取Unsafe
 * 使用的前提是首先要进行获取，本节先从如何获取入手，为大家展示一下如何实例化Unsafe，既然说原子包下面的原子类型都依赖于Unsafe，那么我们参考它就可以了，随便打开一个原子类型的源码（以AtomicInteger源码为例），如下。
 * ...
 * private static final Unsafe unsafe = Unsafe.getUnsafe();
 * ...
 * 看起来很简单，通过调用静态方法Unsafe.getUnsafe()就可以获取一个Unsafe的实例，但是在我们自己的类中执行同样的代码却会抛出SecurityException异常。
 * Exception in thread "main" java.lang.SecuityException: Unsafe
 * 		at sun.misc.Unsafe.getUnsafe(Unsafe.java:90)
 * 		at org.coder.concurrency.programming.juc.automic.UnsafeExample.main(UnsafeExample.java:9)
 * <p>
 * 为什么在AtomicInteger中可以，在我们自己的代码中就不行呢？下面深入源码一探究竟。
 * @CallerSensitive
 * public static Unsafe getUnsafe() {
 * 		Class var0 = Reflection.getCallerClass();
 * 		//如果对getUnsafe方法的调用类不是由系统类加载器加载的，则会抛出异常
 * 		if(!VM.isSystemDomainLoader(var0.getClassLoader())){
 * 			throw new SecurityException("Unsafe");
 * 		}else{
 * 			return theUnsafe;
 * 		}
 * }
 * <p>
 * 通过getUnsafe()方法的源码，我们可以得知，如果调用该方法的类不是被系统类加载器加载的就会抛出异常，通常情况下开发者所开发的Java类都会被应用类加载器进行加载。
 * 在Unsafe类中存在一个Unsafe的实例theUnsafe，该实例是类私有成员，并且在Unsafe类的静态代码块中已经被初始化了，因此我们可以通过反射的方式尝试获取该成员的属性，代码如下所示。
 * private static Unsafe getUnsafe() {
 * 		try{
 * 			Field f = Unsafe.class.getDeclareField("theUnsafe");
 * 			f.setAccessible(true);
 * 			return (Unsafe) f.get(null);
 * 		}catch(Exception e){
 * 			throw new RuntimeException("can't initial the unsafe instance.", e);
 * 		}
 * }
 * <p>
 * 2.8.2 JNI、Java和C/C++混合编程
 * 在Unsafe类中，几乎所有的方法都是native(本地)方法，本地方法是由C/C++实现的，在Java中提供了使用C/C++代码的接口，该接口称为JNI(Java Native Interface)，本节将为大家展示如何使用Java调用C/C++的代码。
 * 注意：本节中的代码都是在笔者的Linux环境下完成的，C/C++代码的编译工具为G++，代码编辑工具为VIM。
 * <p>
 * 1.编写包含本地方法的Java类
 * 第一步，我们需要开发包含本地方法的Java类，定义本地方法接口，并且在该类中加载稍后生成的so库文件，代码如下所示。
 * public class HelloJNI {
 * 		static {
 * 			//加载so库文件，注意该名称需要根据规范命名，后面会说到
 * 			System.loadLibrary("helloJNI");
 * 		}
 * 		//定义本地方法
 * 		public native void sayHello(String name);
 * }
 * Native方法的定义与普通的接口方法定义极为类似，只不过多了一个native关键字用于对该方法进行修饰，在Unsafe类中有大量类似的声明。
 * <p>
 * 2.使用javah 命令生成C++头文件
 * 如果你对JNI很熟悉，那么你可以自行手动编写C++头文件，即使不熟悉也没关系，我们可以借助于JDK提供的javah命令生成C++头文件，
 * 但是在生成头文件之前，首先应该编译我们在第一步编写的java文件，将其编译成class文件。
 * javac HelloJNI.java
 * javah -jni HelloJNI
 * 执行了javah命令之后，你会发现在当前目录下多了一个HelloJNI.h这样一个头文件，代码如下所示。
 * #include <jni.h>
 * #ifndef _Included_HelloJNI
 * #define _Included_HelloJNI
 * #ifdef __cplusplus
 * extern "C"  {
 * 		JNIEXPORT void JNICALL Java_HelloJNI_sayHello (JNIEnv *, jobject, jstring);
 * 		#ifdef __cplusplus
 * }
 * #endif
 * #endif
 * 该头文件是javah命令生成的，可以看到其中引入了另外一个头文件jni.h以及很多条件编译，最重要的是，javah将Java的方法声明翻译成了C++的方法声明，
 * 请注意在翻译的过程中需要符合方法的命名规范Java_JAVA类名_方法名。
 * <p>
 * 3.实现C++代码
 * 头文件已经有了，接下来就需要我们自己开发对应的C++程序了，代码如下所示。
 * #include <iostream>
 * #include <complex>
 * #include "HelloJNI.h"
 * <p>
 * JNIEXPORT void JNICALL Java_HelloJNI_sayHello (JNIEnv *env, jobject obj, jstring name) {
 *		//将jstring转换为string，使用UTF8格式
 * 		std::string s = env->GetStringUTFChars(name, NULL);
 *		//控制台输出
 * 		std::cout<<"Hello "<<s <<std::end;
 * }
 * 在该C++文件中，我们不仅需要引入HelloJNI.h这个头文件，还需要引入C++的输入输出流头文件iostream，然后实现Java_HelloJNI_sayHello方法，简单做一个打印输出即可。
 * <p>
 * 4.生成(shared objects) so文件
 * 一切准备就绪，这个时候就可以使用G++(笔者的G++版本为7.0)命令对我们所开发的C++程序进行编译以及生成so文件的操作了。
 * (1) 编译C++文件，下面的命令执行成功后会多出来一个目标(.o)文件。
 * g++ -c HelloJNI.c -fPIC -D_reentrant -I " $JAVA_HOME/include" -I " $JAVA_HOME/include/linux"
 * (2)生成so文件。
 * g++ -shared HelloJNI.o -o libhelloJNI.so
 * 注意：so文件的命名为lib+，我们在HelloJNI.java静态代码块中加载库名称，在生成了.so文件以后千万不要忘记为.so文件分配可执行权限。
 * <p>
 * 5.在Java中调试C++程序
 * 一切准备就绪，现在可以编写另外一个Java类，然后在main方法中调用HelloJNI的本地方法，就如同我们使用普通的Java类一样。
 * public class HelloExample {
 * 		public static void main(String[] args) {
 * 			HelloJNI jni = new HelloJNI();
 * 			jni.sayHello("alex");
 * 		}
 * }
 * 编译HelloExample.java文件并且运行，你会发现出现了链接库找不到的问题。
 * Exception in thread "main" java.lang.UnsatisfiedLinkError: no helloJNI in java.library.path
 * 		at java.lang.ClassLoader.loadLibrary(ClassLoader.java:1867)
 * 		at java.lang.Runtime.loadLibrary0(Runtime.java:870)
 * 		at java.lang.System.loadLibrary(System.java:1122)
 * 		at org.coder.concurrency.programming.juc._2_atomic.HelloJNI.<clinit>(HelloJNI.java:95)
 * 		at org.coder.concurrency.programming.juc._2_atomic.HelloExample.main(HelloExample.java:6)
 * <p>
 * 根据提示，你需要设置JVM系统属性java.library.path指明链接库所在的地址，但是一般情况下，我们会采用配置操作系统变量的方式来完成。
 * export LD_LIBRARY_PATH="."
 * 将链接库的地址设置为当前路径，再次运行HelloExample，会得到正常的输出，但是该输出来自我们的C++代码，而不是Java程序。
 * crayzer@crayzer:~/jni$ java HelloExample
 * Hello alex
 * 
 * 好了，我们已经顺利地完成了Java程序调用C++接口的编程实践，相信通过本节内容的学习，大家应该明白了Unsafe中声明的本地方法是如何被其他Java程序所使用的了。
 */
public class HelloJNI {
	static {
		//加载so库文件，注意该名称需要根据规范命名，后面会说到
		System.loadLibrary("helloJNI");
	}
	//定义本地方法
	public native void sayHello(String name);
}