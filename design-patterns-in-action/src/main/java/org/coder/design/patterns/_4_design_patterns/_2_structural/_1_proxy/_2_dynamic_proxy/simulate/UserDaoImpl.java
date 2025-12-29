package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserDaoImpl implements IUserDao {
    @Override
    public void save() {
        System.out.println("保存用户数据！");
    }

    @Override
    public void edit() {
        System.out.println("编辑用户数据！");
    }

    @Override
    public void delete() {
        System.out.println("删除用户数据！");
    }

    @Override
    public void saveOrUpdate() {
        System.out.println("修改/添加用户数据！");
    }
}
