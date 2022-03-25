package com.hlkj.distributelockdemo.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Lixiangping
 * @createTime 2022年03月25日 16:48
 * @decription: 单体锁局限性测试
 */
@RestController
@Slf4j
public class SimpleLockController {

    private Lock lock = new ReentrantLock();

    /**
     * 同一JVM，启动应用：锁有效
     * 不同JVM，启动两个端口：锁失效
     */
    @GetMapping("/simpleLock")
    public void simpleLock() {
        log.info("进入了方法");
        lock.lock();
        log.info("进入了锁");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();

    }

}
