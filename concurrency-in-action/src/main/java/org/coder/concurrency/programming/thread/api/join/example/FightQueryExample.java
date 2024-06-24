package org.coder.concurrency.programming.thread.api.join.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 输出结果：
 * <p>
 * [CSA]-query from SH to BJ
 * [HNA]-query from SH to BJ
 * [CEA]-query from SH to BJ
 * The fight: [HNA] list query successful
 * The fight: [CEA] list query successful
 * The fight: [CSA] list query successful
 * =================result===============
 * [CSA]-5
 * [CEA]-2
 * [HNA]-1
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class FightQueryExample {

    private static List<String> fightCompany = Arrays.asList("CSA", "CEA", "HNA");

    public static void main(String[] args) {
        final List<String> results = search("SH", "BJ");
        System.out.println("=================result===============");
        results.forEach(System.out::println);
    }

    private static List<String> search(String original, String dest) {

        final List<String> result = new ArrayList<>();
        List<FightQueryTask> tasks = fightCompany.stream().map(f -> createSearchTask(f, original, dest)).collect(Collectors.toList());

        tasks.forEach(Thread::start);

        tasks.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 再此之前，当前线程会阻塞住，获取每图个查询线程的结果，并且加入到result中
        tasks.stream().map(FightQueryTask::get).forEach(result::addAll);
        return result;
    }

    private static FightQueryTask createSearchTask(String fight, String original, String dest) {
        return new FightQueryTask(fight, original, dest);
    }
}
