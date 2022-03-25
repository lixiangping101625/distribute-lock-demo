package com.hlkj.distributelockdemo.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Lixiangping
 * @createTime 2022年03月25日 17:30
 * @decription:
 */
@Entity
@Data
@Table(name = "distribute-lock", schema = "distribute", catalog = "")
public class DistributeLock {
    @Id
    private int id;
    private String businessCode;
    private String businessName;

}
