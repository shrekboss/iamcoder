package org.coder.err.programming._1_code_chapter.equals.lombokequals;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("lombokequals")
public class LombokEquealsController {

    @GetMapping("test1")
    public void test1() {
        Person person1 = new Person("crayzer", "001");
        Person person2 = new Person("Joseph", "001");
        // false
        // true @EqualsAndHashCode.Exclude name
        log.info("person1.equals(person2) ? {}", person1.equals(person2));
    }

    @GetMapping("test2")
    public void test2() {
        Employee employee1 = new Employee("crayzer", "001", "bkjk.com");
        Employee employee2 = new Employee("Joseph", "002", "bkjk.com");
        // false
        // true @EqualsAndHashCode(callSuper = true)
        log.info("employee1.equals(employee2) ? {}", employee1.equals(employee2));
    }

    // @Data 注解会帮我们实现 equals 和 hashcode 方法
    @Data
    class Person {
        // @EqualsAndHashCode.Exclude
        private String name;
        private String identity;

        public Person(String name, String identity) {
            this.name = name;
            this.identity = identity;
        }
    }

    @Data
            // @EqualsAndHashCode(callSuper = true)
    class Employee extends Person {

        private String company;

        public Employee(String name, String identity, String company) {
            super(name, identity);
            this.company = company;
        }
    }
}
