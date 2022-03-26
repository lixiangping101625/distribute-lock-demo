package com.hlkj.distributelockdemo.api.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Lixiangping
 * @createTime 2022年03月26日 15:12
 * @decription:
 */
@Slf4j
public class RedisLock implements AutoCloseable{

    private RedisTemplate redisTemplate;
    private String key;
    private String value;
    //过期时间：秒
    private int expireTime;

    public RedisLock(RedisTemplate redisTemplate, String key, int expireTime) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.value = UUID.randomUUID().toString();
        this.expireTime = expireTime;
    }

    //获取分布式锁
    public boolean getLock(){
        RedisCallback<Boolean> redisCallback = connection -> {
            //设置NX
            RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();
            //设置过期时间
            Expiration expiration = Expiration.seconds(expireTime);
            //序列化key和value：注意不能简单使用getBytes()方法
            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
            byte[] redisValue = redisTemplate.getKeySerializer().serialize(value);
            //执行setnx操作
            Boolean result = connection.set(redisKey, redisValue, expiration, setOption);
            return result;
        };
        //获取分布式锁
        Boolean lock = (Boolean) redisTemplate.execute(redisCallback);
        return lock;
    }

    public boolean unLock(){
        String script = "if redis.call(\"get\",KEYS[1])==ARGV[1] then\n" +
                "\treturn redis.call(\"del\", KEYS[1])\n" +
                "else\n" +
                "\treturn 0\n" +
                "end";
        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);

        List keys = Arrays.asList(key);
        Boolean result = (Boolean) redisTemplate.execute(redisScript, keys, value);

        return result;
    }

    @Override
    public void close() throws Exception {
        boolean b = unLock();
        if (b)
            log.info("释放锁成功~");
    }
}
