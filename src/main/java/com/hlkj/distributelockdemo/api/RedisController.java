package com.hlkj.distributelockdemo.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Lixiangping
 * @createTime 2022年03月26日 13:57
 * @decription: 基于redis实现分布式锁
 */
@RestController
@Slf4j
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/redisLock")
    public String redisLock() {
        log.info("进入方法~");

        String key = "redisKey";
        String value = UUID.randomUUID().toString();

        RedisCallback<Boolean> redisCallback = connection -> {
            //设置NX
            RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();
            //设置过期时间
            Expiration expiration = Expiration.seconds(30);
            //序列化key和value：注意不能简单使用getBytes()方法
            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
            byte[] redisValue = redisTemplate.getKeySerializer().serialize(value);
            //执行setnx操作
            Boolean result = connection.set(redisKey, redisValue, expiration, setOption);
            return result;
        };
        //获取分布式锁
        Boolean lock = (Boolean) redisTemplate.execute(redisCallback);
        if (lock) {
            log.info("获得了锁~");
            try {
                //模拟执行操作
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {//释放锁
                String script = "if redis.call(\"get\",KEYS[1])==ARGV[1] then\n" +
                        "\treturn redis.call(\"del\", KEYS[1])\n" +
                        "else\n" +
                        "\treturn 0\n" +
                        "end";
                RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);

                List keys = Arrays.asList(key);
                Boolean result = (Boolean) redisTemplate.execute(redisScript, keys, value);

                log.info("释放锁的结果：" + result);
            }
        }
        log.info("方法执行完成");
        return "方法执行完成";
    }

}
