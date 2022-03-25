package com.hlkj.distributelockdemo.repository;

import com.hlkj.distributelockdemo.pojo.DistributeLock;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Lixiangping
 * @createTime 2022年03月25日 17:31
 * @decription:
 */

public interface DistributeLockRepository extends JpaRepository<DistributeLock, Integer> {
}
