package org.example.withoutspring.redis;

import redis.clients.jedis.Jedis;

/**
 * @author fengyadong
 * @date 2022/8/5 13:56
 * @Description
 */
public class Test {

    public static void main(String[] args) {
        Jedis jedis = RedisUtils.getRedis();
        jedis.set("test", "redisutils");
        RedisUtils.returnJedis(jedis);
        Jedis jedis1 = RedisUtils.getRedis();
        System.out.println(jedis1.get("test"));
        RedisUtils.returnJedis(jedis1);
    }

}
