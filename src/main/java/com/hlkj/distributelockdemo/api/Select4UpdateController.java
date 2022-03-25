package com.hlkj.distributelockdemo.api;

import com.hlkj.distributelockdemo.pojo.DistributeLock;
import com.hlkj.distributelockdemo.repository.DistributeLockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lixiangping
 * @createTime 2022年03月25日 18:15
 * @decription: select for update 数据库锁实现分布式数据安全性
 */
@RestController
@Slf4j
public class Select4UpdateController {

    @Autowired
    private DistributeLockRepository distributeLockRepository;

    /**
     * 数据库悲观锁实现集群部署下资源锁的有效性
     *      查询时就对数据库加了锁，所以在一个线程未释放锁之前下一个线程查询时是无法获得锁的。这种方式不推荐，性能极低！
     * @return
     */
    @GetMapping("select4Update")
    @Transactional(rollbackFor = Exception.class)//必须要有事务。
    public List<DistributeLock> select4Update(){
        log.info("进入了方法");
        List<DistributeLock> list = distributeLockRepository.queryForUpdate();
        if (list.size()==0)
            throw new RuntimeException("数据不存在");
        log.info("进入了锁");
        try {
            Thread.sleep(20000);//稍微短点
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }
}
