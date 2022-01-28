package com.meteor.app.entity.lock;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
@Data
public class OptimisticLockEntity {
    @Id
    @GeneratedValue
    private Long id;
    private long sum;
    @Version
    private long version;
}