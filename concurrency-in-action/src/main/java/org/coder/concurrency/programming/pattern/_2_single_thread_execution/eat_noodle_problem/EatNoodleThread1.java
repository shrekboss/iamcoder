package org.coder.concurrency.programming.pattern._2_single_thread_execution.eat_noodle_problem;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class EatNoodleThread1 extends Thread {

    private final String name;
    private final TablewarePair tablewarePair;

    public EatNoodleThread1(String name, TablewarePair tablewarePair) {
        this.name = name;
        this.tablewarePair = tablewarePair;
    }

    @Override
    public void run() {
        while (true) {
            this.eat();
        }
    }

    private void eat() {
        synchronized (tablewarePair) {
            System.out.println(name + " take up " + tablewarePair.getLeftTool() + " (left) ");
            System.out.println(name + " take up " + tablewarePair.getRightTool() + " (right) ");
            System.out.println(name + " is eating now.");
            System.out.println(name + " put down " + tablewarePair.getRightTool() + " (right) ");
            System.out.println(name + " put down " + tablewarePair.getLeftTool() + " (left) ");
        }
    }

    public static void main(String[] args) {
        Tableware fork = new Tableware("fork");
        Tableware knife = new Tableware("knife");
        TablewarePair tablewarePair = new TablewarePair(fork, knife);

        // 死锁
        new EatNoodleThread1("A", tablewarePair).start();
        new EatNoodleThread1("B", tablewarePair).start();
    }
}
