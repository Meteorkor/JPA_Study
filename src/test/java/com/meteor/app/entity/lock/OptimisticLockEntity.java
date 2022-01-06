package com.meteor.app.entity.lock;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
@Data
public class OptimisticLockEntity {
    @Id
    private Long id;
    private long sum;
    @Version
    private long version;
}