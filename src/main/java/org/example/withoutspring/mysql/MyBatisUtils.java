package org.example.withoutspring.mysql;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.withoutspring.redis.RedisUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author fengyadong
 * @date 2022/5/26 16:16
 * @Description
 */
public class MyBatisUtils {

    private static SqlSessionFactory sqlSessionFactory;

    static {

        try {
            InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取sqlSession对象
     * @return
     */
    public static SqlSession getSqlSession() {
        //根据SqlSessionFactory创建SqlSession
        return sqlSessionFactory.openSession();
    }

}
