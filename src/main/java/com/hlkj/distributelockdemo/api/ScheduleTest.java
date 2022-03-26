package com.hlkj.distributelockdemo.api;

import com.hlkj.distributelockdemo.api.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Lixiangping
 * @createTime 2022年03月26日 15:46
 * @decription: 测试redis分布式锁对集群部署场景下的定时任务
 */
@Component
@Slf4j
public class ScheduleTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 未使用redis 分布式锁：
     *  8080服务：
     *      2022-03-26 15:49:00.005  INFO 19044 --- [   scheduling-1] c.h.distributelockdemo.api.ScheduleTest  : 模拟发送短信...
     *  8081服务：
     *      2022-03-26 15:49:00.005  INFO 21284 --- [   scheduling-1] c.h.distributelockdemo.api.ScheduleTest  : 模拟发送短信...
     *
     *  使用redis分布式锁：
     *   8080服务：
     *      2022-03-26 15:55:00.017  INFO 1772 --- [   scheduling-1] c.h.distributelockdemo.api.ScheduleTest  : 模拟发送短信...
     *   081服务：
     *      2022-03-26 15:55:00.017  INFO 3212 --- [   scheduling-1] c.h.distributelockdemo.api.ScheduleTest  : 获取锁失败，不进行短信发送
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendSms() {
        try(RedisLock redisLock = new RedisLock(redisTemplate, "redisKey", 30)) {
            if (redisLock.getLock()) {
                log.info("模拟发送短信...");
            }
            log.info("获取锁失败，不进行短信发送");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
