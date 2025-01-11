package org.coder.concurrency.programming.juc._6_java_stream;

public class Production {

    private final String name;
    private final double price;

    Production(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "{name=" + name + ", price=" + price + "}";
    }
}