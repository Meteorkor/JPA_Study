package com.meteor.app;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.meteor.app.entity.lock.DynamicUpdateOptimisticLockEntity;

@DataJpaTest
public class DynamicUpdateTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void dynamicUpdateTest() {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        long id = 0;
        {
            DynamicUpdateOptimisticLockEntity optimisticLockEntity = new DynamicUpdateOptimisticLockEntity();
            optimisticLockEntity.setSum(1000);
            insertDynamicUpdateOptimisticLockEntity(optimisticLockEntity);
            id = optimisticLockEntity.getId();
        }

        try {
            transaction.begin();
            DynamicUpdateOptimisticLockEntity optimisticLockEntity = entityManager.find(
                    DynamicUpdateOptimisticLockEntity.class, id);
            optimisticLockEntity.setSum(optimisticLockEntity.getSum() + 100);
            /* not @DynamicUpdate
             update dynamic_update_optimistic_lock_entity
                set
                    sum=?,
                    update_test_field=?,
                    version=?
                where
                    id=?
                    and version=?
            */

            /* @DynamicUpdate
            update dynamic_update_optimistic_lock_entity
                set
                    sum=?,
                    version=?
                where
                    id=?
                    and version=?
            */
            transaction.commit();
        } catch (RollbackException t) {
            t.printStackTrace();
            transaction.rollback();
        } finally {
            entityManager.close();
        }

    }

    void insertDynamicUpdateOptimisticLockEntity(DynamicUpdateOptimisticLockEntity optimisticLockEntity) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(optimisticLockEntity);
            transaction.commit();
        } catch (RollbackException t) {
            t.printStackTrace();
            transaction.rollback();
        } finally {
            entityManager.close();
        }

    }
}
