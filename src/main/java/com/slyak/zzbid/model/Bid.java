package com.slyak.zzbid.model;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * .
 *
 * @author stormning 2017/12/19
 * @since 1.3.0
 */
@Entity
@Data
@Table(name = "t_bid")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "bid")
public class Bid {

    @Id
    //分包编号
    private String id;

    private String realPkgId;

    //类别
    private String firstType;

    //单位
    private String dept;

    //开始时间
    private String startTime;

    //结束时间
    private String endTime;

    //预算
    private String budget;

    //任务开始时间
    private long taskTime = 0;

    //自动投标时间
    private long bidTime = 0;

    //投标快照
    @Column(length = 3000)
    private String snapshot;
}
