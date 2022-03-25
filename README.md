### 单体应用锁（synchronized关键字和lock锁）的局限性
##### 严格上说，一个线上产品的稳定性和项目架构有着非常大的联系。项目上线后，作为架构师应该考虑服务的稳定性。所以项目部署方式不应该是单节点部署，而是集群部署。
    
### *由于synchronized关键字和lock锁由于不能够跨JVM，所以他们对集群部署的方式无效！！*
    
##### 单体应用锁测试demo：
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
#### 运行结果：
    1、同一JVM：可以看到线程【nio-8080-exec-2】一开始并未执行lock锁定的代码块，而是等上一线程执行完成释放锁后，才执行
        2022-03-25 16:53:42.268  INFO 61108 --- [nio-8080-exec-1] c.h.d.api.SimpleLockController           : 进入了方法
        2022-03-25 16:53:42.268  INFO 61108 --- [nio-8080-exec-1] c.h.d.api.SimpleLockController           : 进入了锁
        2022-03-25 16:54:07.581  INFO 61108 --- [nio-8080-exec-2] c.h.d.api.SimpleLockController           : 进入了方法
        2022-03-25 16:54:42.279  INFO 61108 --- [nio-8080-exec-2] c.h.d.api.SimpleLockController           : 进入了锁    
        
    2、不同JVM：启动两个端口.可以看到都进入了方法进入了锁
        2022-03-25 16:58:42.435  INFO 61108 --- [nio-8080-exec-7] c.h.d.api.SimpleLockController           : 进入了方法
        2022-03-25 16:58:42.435  INFO 61108 --- [nio-8080-exec-7] c.h.d.api.SimpleLockController           : 进入了锁
        
        2022-03-25 16:58:46.771  INFO 41548 --- [nio-8081-exec-1] c.h.d.api.SimpleLockController           : 进入了方法
        2022-03-25 16:58:46.771  INFO 41548 --- [nio-8081-exec-1] c.h.d.api.SimpleLockController           : 进入了锁
            
            
        