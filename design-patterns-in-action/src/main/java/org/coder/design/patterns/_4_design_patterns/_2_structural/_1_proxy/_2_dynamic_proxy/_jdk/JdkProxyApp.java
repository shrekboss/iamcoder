package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy._jdk;

import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.IUserDao;
import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.UserDaoImpl;
import sun.misc.ProxyGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
public class JdkProxyApp {
    public static void main(String[] args) throws IOException {
        UserDaoImpl userDaoImpl = new UserDaoImpl();
        IUserDao proxy = (IUserDao) new UserDaoImplWithJdkProxy(userDaoImpl).getProxyInstance();
        System.out.println(proxy);

        proxy.save();
        proxy.edit();

        //获取字节码内容
        getByteContent();
    }


    /**
     * _jdk 实现动态代理为什么需要接口
     * 原因：public final class $Proxy0 extends Proxy implements IUserDao
     * java 是单继承的 那么代理类要实现方法的动态代理只能通过实现接口的方式
     *
     * @throws IOException
     */
    private static void getByteContent() {
        byte[] bytes = ProxyGenerator.generateProxyClass("$Proxy", new Class[]{IUserDao.class});

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("$Proxy.class");
            fos.write(bytes);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
