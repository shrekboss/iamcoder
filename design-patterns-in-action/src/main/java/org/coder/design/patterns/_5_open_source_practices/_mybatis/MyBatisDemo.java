package org.coder.design.patterns._5_open_source_practices._mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.coder.design.patterns._5_open_source_practices._mybatis.simulate.UserDo;
import org.coder.design.patterns._5_open_source_practices._mybatis.simulate.UserMapper;

import java.io.IOException;
import java.io.Reader;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class MyBatisDemo {

    /**
     * Demo 伪代码实例
     */
    public static void main(String[] args) throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis.xml");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession session = sessionFactory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        UserDo userDo = userMapper.selectById(8);

        //...
    }
}
