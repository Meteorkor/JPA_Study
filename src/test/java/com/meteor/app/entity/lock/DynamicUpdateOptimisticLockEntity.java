package com.meteor.app.entity.lock;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLocking;

import lombok.Data;

@Entity
@Data
@DynamicUpdate
@OptimisticLocking
public class DynamicUpdateOptimisticLockEntity {
    @Id
    @GeneratedValue
    private Long id;
    private long sum;
    private String updateTestField;
    @Version
    private long version;
}
