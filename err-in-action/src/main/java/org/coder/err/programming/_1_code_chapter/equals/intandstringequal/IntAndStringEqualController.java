package org.coder.err.programming._1_code_chapter.equals.intandstringequal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@Slf4j
@RequestMapping("intandstringequal")
public class IntAndStringEqualController {

    List<String> list = new ArrayList<>();

    /**
     * http://localhost:45678/intandstringequal/stringcompare
     */
    @GetMapping("stringcompare")
    public void stringcomare() {
        String a = "1";
        String b = "1";
        log.info("\nString a = \"1\";\n" +
                "String b = \"1\";\n" +
                "a == b ? {}", a == b); //true

        String c = new String("2");
        String d = new String("2");
        log.info("\nString c = new String(\"2\");\n" +
                "String d = new String(\"2\");" +
                "c == d ? {}", c == d); //false

        // intern 让字符串进行驻留
        String e = new String("3").intern();
        String f = new String("3").intern();
        log.info("\nString e = new String(\"3\").intern();\n" +
                "String f = new String(\"3\").intern();\n" +
                "e == f ? {}", e == f); //true

        String g = new String("4");
        String h = new String("4");
        log.info("\nString g = new String(\"4\");\n" +
                "String h = new String(\"4\");\n" +
                " g.equals(h) ? {}", g.equals(h)); //true
    }

    /**
     * http://localhost:45678/intandstringequal/internperformance
     */
    @GetMapping("internperformance")
    public int internperformance(@RequestParam(value = "size", defaultValue = "10000000") int size) {
        //-XX:+PrintStringTableStatistics
        //-XX:StringTableSize=10000000
        long begin = System.currentTimeMillis();
        list = IntStream.rangeClosed(1, size)
                .mapToObj(i -> String.valueOf(i).intern())
                .collect(Collectors.toList());
        log.info("size:{} took:{}", size, System.currentTimeMillis() - begin);
        return list.size();
    }

    /**
     * http://localhost:45678/intandstringequal/intcompare
     */
    @GetMapping("intcompare")
    public void intcompare() {

        // 默认情况下会缓存[-128, 127]的数值
        Integer a = 127; //Integer.valueOf(127)
        Integer b = 127; //Integer.valueOf(127)
        log.info("\nInteger a = 127;\n" +
                "Integer b = 127;\n" +
                "a == b ? {}", a == b);    // true

        Integer c = 128; //Integer.valueOf(128)
        Integer d = 128; //Integer.valueOf(128)
        log.info("\nInteger c = 128;\n" +
                "Integer d = 128;\n" +
                "c == d ? {}", c == d);   //false
        //设置 Vm options: -XX:AutoBoxCacheMax=1000再试试

        Integer e = 127; //Integer.valueOf(127)
        Integer f = new Integer(127); //new instance
        log.info("\nInteger e = 127;\n" +
                "Integer f = new Integer(127);\n" +
                "e == f ? {}", e == f);   //false

        Integer g = new Integer(127); //new instance
        Integer h = new Integer(127); //new instance
        log.info("\nInteger g = new Integer(127);\n" +
                "Integer h = new Integer(127);\n" +
                "g == h ? {}", g == h);  //false

        Integer i = 128; //unbox
        int j = 128;
        log.info("\nInteger i = 128;\n" +
                "int j = 128;\n" +
                "i == j ? {}", i == j); //true

    }

    /**
     * http://localhost:45678/intandstringequal/enumcompare
     */
    @PostMapping("enumcompare")
    public void enumcompare(@RequestBody OrderQuery orderQuery) {
        StatusEnum statusEnum = StatusEnum.DELIVERED;
        // 枚举和入参 OrderQuery 中的 status 都是包装类型，所以通过 == 判等肯定是有问题的
        log.info("orderQuery:{} statusEnum:{} result:{}", orderQuery, statusEnum, statusEnum.status == orderQuery.getStatus());
    }

    enum StatusEnum {
        CREATED(1000, "已创建"),
        PAID(1001, "已支付"),
        DELIVERED(1002, "已送到"),
        FINISHED(1003, "已完成");

        private final Integer status; //注意这里的Integer
        private final String desc;

        StatusEnum(Integer status, String desc) {
            this.status = status;
            this.desc = desc;
        }
    }
}
