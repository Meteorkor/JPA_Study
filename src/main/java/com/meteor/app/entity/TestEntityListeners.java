package com.meteor.app.entity;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.stereotype.Component;

@Component
public class TestEntityListeners {
    public static final String PRE = "pre";
    public static final String POST = "post";

    @PrePersist
    public void preInsertLogging(EntityListenersTestEntity entity) {
        entity.setMemo(PRE);
    }

    @PostPersist
    public void postInsertLogging(EntityListenersTestEntity entity) {
        entity.setMemo(POST);
    }

    @PostLoad
    public void postLoadLogging(EntityListenersTestEntity entity) {

    }

    @PreUpdate
    public void preUpdateLogging(EntityListenersTestEntity entity) {

    }

    @PostUpdate
    public void postUpdateLogging(EntityListenersTestEntity entity) {

    }
    //@PreRemove
    //@PostRemove
}
