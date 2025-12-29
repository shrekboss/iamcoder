package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._1_static_proxy._static;

import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.IUserDao;
import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.UserDaoImpl;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserDaoImplProxy implements IUserDao {

    private UserDaoImpl target;

    public UserDaoImplProxy(UserDaoImpl target) {
        this.target = target;
    }

    @Override
    public void save() {
        System.out.println("开始保存事务...");
        target.save();
        System.out.println("提交保存事务...");
    }

    @Override
    public void edit() {
        System.out.println("开始编辑事务...");
        target.save();
        System.out.println("提交编辑事务...");
    }

    @Override
    public void delete() {
        System.out.println("开始删除事务...");
        target.save();
        System.out.println("提交删除事务...");
    }

    @Override
    public void saveOrUpdate() {
        System.out.println("开始保存/更新事务...");
        target.save();
        System.out.println("提交保存/更新事务...");
    }
}
