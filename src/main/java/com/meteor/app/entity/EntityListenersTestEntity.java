package com.meteor.app.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
@EntityListeners(TestEntityListeners.class)
@Data
public class EntityListenersTestEntity {

    @GeneratedValue
    @Id
    private Long id;
    private String name;
    private String memo;
    private LocalDateTime createdDate;
}
