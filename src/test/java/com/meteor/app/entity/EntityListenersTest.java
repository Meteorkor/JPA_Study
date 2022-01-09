package com.meteor.app.entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@DataJpaTest
public class EntityListenersTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void entityListenerPersistTest() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            EntityListenersTestEntity entity = new EntityListenersTestEntity();
            entityManager.persist(entity);
            Assertions.assertThat(entity.getMemo()).isEqualTo(TestEntityListeners.PRE);

            transaction.commit();
            EntityListenersTestEntity byId = findById(entity.getId());
            Assertions.assertThat(byId.getMemo()).isEqualTo(TestEntityListeners.PRE);
            Assertions.assertThat(entity.getMemo()).isEqualTo(TestEntityListeners.POST);
        } finally {
            entityManager.close();
        }
    }

    @Test
    void entityListenerFindTest() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            EntityListenersTestEntity entity = new EntityListenersTestEntity();
            entityManager.persist(entity);
            Assertions.assertThat(entity.getMemo()).isEqualTo(TestEntityListeners.PRE);

            transaction.commit();
            EntityListenersTestEntity byId = findById(entity.getId());
            Assertions.assertThat(byId.getMemo()).isEqualTo(TestEntityListeners.PRE);
            Assertions.assertThat(entity.getMemo()).isEqualTo(TestEntityListeners.POST);
        } finally {
            entityManager.close();
        }
    }

    private EntityListenersTestEntity findById(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            EntityListenersTestEntity entity1 = entityManager.find(EntityListenersTestEntity.class, id);
            return entity1;
        } finally {
            entityManager.close();
        }
    }
}
