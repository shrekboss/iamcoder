package org.coder.concurrency.programming.thread.api.join.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class FightQueryTask extends Thread implements FightQuery{

    private final String original;
    private final String destination;
    private final List<String> fightList = new ArrayList<String>();

    public FightQueryTask(String airline, String original, String destination) {
        super("[" + airline + "]");
        this.original = original;
        this.destination = destination;
    }

    @Override
    public void run() {
        System.out.printf("%s-query from %s to %s \n", getName(), original, destination);
        int randomValue = ThreadLocalRandom.current().nextInt(10);

        try {
            // 模拟查询接口正在执行所消耗的时长
            TimeUnit.SECONDS.sleep(randomValue);

            this.fightList.add(getName() + "-" + randomValue);
            System.out.printf("The fight: %s list query successful\n", getName());
        } catch (InterruptedException e) {

        }
    }

    @Override
    public List<String> get() {
        return this.fightList;
    }


}
