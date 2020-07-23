package com.meteor.app.entity.lock;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

//javax.persistence.Version
//org.springframework.data.annotation.Version;
@Entity
@Data
public class OptimisticLockEntity {
    @Id
    private Long id;
    private long sum;
    @Version
//    @Version
    private long version;
}