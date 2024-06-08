package org.coder.design.patterns._1_oop._4_polymorphism;

public class LinkedList implements Iterator {
    private LinkedListNode head;

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String next() {
        return null;
    }

    @Override
    public String remove() {
        return null;
    }

    private class LinkedListNode {
    }
}
