package org.coder.concurrency.programming.jmh;

import java.net.URL;
import java.net.URLClassLoader;

public class AlexClassLoader extends URLClassLoader {
    private final byte[] bytes;

    public AlexClassLoader(byte[] bytes) {
        super(new URL[0], ClassLoader.getSystemClassLoader());
        this.bytes = bytes;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return defineClass(name, bytes, 0, bytes.length);
    }
}