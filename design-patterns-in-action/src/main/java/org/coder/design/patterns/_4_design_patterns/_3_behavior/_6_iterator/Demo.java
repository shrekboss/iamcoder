package org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Demo {
    public static void main(String[] args) {

        List<String> names = new ArrayList();
        names.add("a");
        names.add("b");
        names.add("c");
        names.add("d");

        Iterator<String> iterator = names.iterator1();
//        while (iterator.hasNext()) {
//            System.out.println(iterator.currentItem());
//            iterator.next();
//        }
        iterator.next();
        names.remove("a");
        iterator.remove();
        iterator.next();
    }
}
