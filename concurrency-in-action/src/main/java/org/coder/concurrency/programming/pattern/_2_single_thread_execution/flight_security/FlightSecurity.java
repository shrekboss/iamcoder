package org.coder.concurrency.programming.pattern._2_single_thread_execution.flight_security;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class FlightSecurity {

    private int count = 0;

    // 登机牌
    private String boardingPass = "null";
    // 身份证
    private String idCard = "null";

    // 非线程安全
//    public void pass(String boardingPass, String idCard) {
    // 线程安全
    public synchronized void pass(String boardingPass, String idCard) {
        this.boardingPass = boardingPass;
        this.idCard = idCard;
        this.count++;
        check();
    }

    private void check() {
        if (boardingPass.charAt(0) != idCard.charAt(0)) {
            throw new RuntimeException("====Exception====" + toString());
        }
    }

    @Override
    public String toString() {
        return "The " + count + " passengers,boardingPass  [" + boardingPass + "],idCard [" + idCard + "]";
    }
}
