package com.hlkj.distributelockdemo.repository;

import com.hlkj.distributelockdemo.pojo.DistributeLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * @author Lixiangping
 * @createTime 2022年03月25日 17:31
 * @decription:
 */

public interface DistributeLockRepository extends JpaRepository<DistributeLock, Integer> {

//    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(nativeQuery = true, value = "SELECT * FROM distribute_lock for update")//原生sql
    List<DistributeLock> queryForUpdate();

}
