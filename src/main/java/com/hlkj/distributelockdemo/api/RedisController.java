package com.hlkj.distributelockdemo.api;

import com.hlkj.distributelockdemo.api.lock.RedisLock;
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
        try(RedisLock redisLock = new RedisLock(redisTemplate, "redisKey", 30)) {
            //获取分布式锁
            if (redisLock.getLock()) {
                log.info("获得了锁~");
                Thread.sleep(15000);//模拟执行操作
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("方法执行完成");
        return "方法执行完成";
    }

}
