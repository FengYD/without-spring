package org.example.withoutspring.redis;

import org.example.withoutspring.annotations.MyService;
import redis.clients.jedis.Jedis;

/**
 * @author fengyadong
 * @date 2022/5/26 15:08
 * @Description
 */
@MyService(value = "redisTemplate")
public class RedisTemplate {

    public void set(String key, String value) {
        Jedis jedis = RedisUtils.getRedis();
        jedis.set(key, value);
        RedisUtils.returnJedis(jedis);
    }

    public String get(String key) {
        Jedis jedis = RedisUtils.getRedis();
        String value = jedis.get(key);
        RedisUtils.returnJedis(jedis);
        return value;
    }

}
