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
---            
### 分布式锁
##### 方案一： 借助数据库悲观锁实现：`select ... for update`  语句对数据资源加锁。其他线程直接等待。
    直接在Navicat中测试：
        1、数据准备
        DROP TABLE IF EXISTS `distribute_lock`;
        CREATE TABLE `distribute-lock`  (
          `id` int(11) NOT NULL AUTO_INCREMENT,
          `business_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
          `business_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
          PRIMARY KEY (`id`) USING BTREE
        ) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
        
        -- ----------------------------
        -- Records of distribute-lock
        -- ----------------------------
        INSERT INTO `distribute_lock` VALUES (1, 'demo', '测试demo');
        2、关闭会话的事务自动提交
            mysql事务默认是自动提交，首先在Navicat中打开两个查询窗口，分别执行 `set @@autocommit=0;`关闭会话自动提交事务。
            不然就算执行select ... for update，由于查询速度快所以效果不明显。
        3、两个窗口执行`select * from distribute for update`语句
            结果：第一个窗口查询到数据。但第二个窗口为查询到数据！
            原因分析：是因为`select * from distribute for update`语句对所聚酷查询进行了加锁，第一个查询完成后为提交事务，导致
            第二个窗口查询为获得到数据库的锁。其实这就是所说的数据库悲观锁。
            
    JPA测试：
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
                //休眠一分钟，在这一分钟内在其他端口（不同JVM）发起其他请求依然是无法查询的，说明基于数据库的悲观锁
                //      是可以解决集群部署下的超卖问题的。
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return list;
        }    
        