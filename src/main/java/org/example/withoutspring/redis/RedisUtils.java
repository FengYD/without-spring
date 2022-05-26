package org.example.withoutspring.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author fengyadong
 * @date 2022/5/26 14:29
 * @Description
 */
public class RedisUtils {

    private static String host;

    private static String port;

    private static String password;

    //jedis 连接池
    private static JedisPool jedisPool = null;

    static {
        InputStream inputStream = RedisUtils.class.getClassLoader().getResourceAsStream("redis.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("加载配置文件失败");
        }
        RedisUtils.host = properties.getProperty("host", "127.0.0.1");
        RedisUtils.port = properties.getProperty("port", "6379");
        RedisUtils.password = properties.getProperty("password");
        try {
            JedisPoolConfig conf = new JedisPoolConfig();
            //设置连接实例最大数目
            conf.setMaxTotal(20);
            //设置最多多少空闲的 jedis 实例
            conf.setMaxIdle(5);
            //设置是否提前进行测试借用
            conf.setTestOnBorrow(true);

            //新建 jedis 连接池
            jedisPool = new JedisPool(conf, host, Integer.parseInt(port), 10000, password);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 获取 jedis 实例来操作数据，每次使用完要将连接返回给连接池 jedis.close()
     * @return
     */
    public synchronized static Jedis getRedis() {
        try {
            if(jedisPool != null) {
                //获取 jedis 实例
                Jedis jedis = jedisPool.getResource();
                return jedis;
            }
            else{
                System.out.println("没有找到 Jedis 连接池！");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
     * 用来回收 Jedis 对象资源，用户需要用到此方法释放资源，否则一直占用资源
     * @param Jedis jedis
     */
    public synchronized static void returnJedis(Jedis jedis) {
        try {
            if(jedis != null) {
                //回收 jedis 对象资源
                jedisPool.returnResource(jedis);
                System.out.println("Jedis 被成功回收！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
