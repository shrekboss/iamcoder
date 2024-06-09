package org.coder.design.patterns._2_design_principle._2_ocp;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Demo {

    public static void main(String[] args) {
        ApiStatInfo apiStatInfo = new ApiStatInfo();
        // ...省略设置apiStatInfo数据值的代码
        apiStatInfo.setRequestCount(1000);
        apiStatInfo.setDurationOfSeconds(60);
        apiStatInfo.setErrorCount(9);
        ApplicationContext.getInstance().getAlert().check(apiStatInfo);
    }
}
