package com.meteor.app;

import com.meteor.app.entity.lock.OptimisticLockEntity;
import com.meteor.app.entity.lock.PessimisticLockEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class LockTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private final int expectSum = 1000;

    @Test
    void optimisticLocalStaticLock() throws InterruptedException {
        long id = 14;
        optimisticInsert(id);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < expectSum; i++) {
            executorService.submit(() -> {
                try {
                    //다중 서버에서는 당연히 정상동작 안함
                    synchronized (EntityManagerFactory.class) {
                        optimisticSimpleUp(id);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        OptimisticLockEntity optimisticLockEntity = entityManagerFactory.createEntityManager().find(OptimisticLockEntity.class, id);
        Assertions.assertEquals(expectSum, optimisticLockEntity.getSum());
        System.out.println("optimisticLockEntity : " + optimisticLockEntity);
    }

    @Test
    void optimisticLock() throws InterruptedException {
        long id = 24;
        optimisticInsert(id);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < expectSum; i++) {
            executorService.submit(() -> {
                try {
                    optimisticUp(id);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        }
//        executorService.shutdown();
        Thread.sleep(1000*5);
        //FIXME submit은 됬지만 스케줄안된경우..
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        OptimisticLockEntity optimisticLockEntity = entityManagerFactory.createEntityManager().find(OptimisticLockEntity.class, id);
        System.out.println("optimisticLockEntity : " + optimisticLockEntity);
        Assertions.assertEquals(expectSum, optimisticLockEntity.getSum());
    }

    @Test
    void pessimisticLock() throws InterruptedException {
        long id = 34;
        pessimisticInsert(id);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < expectSum; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticUp(id);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        PessimisticLockEntity pessimisticLockEntity = entityManagerFactory.createEntityManager().find(PessimisticLockEntity.class, id);
        System.out.println("pessimisticLockEntity : " + pessimisticLockEntity);
        Assertions.assertEquals(expectSum, pessimisticLockEntity.getSum());
    }

    private void pessimisticUp(long id) throws InterruptedException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

//            PessimisticLockEntity pessimisticLockEntity = entityManager.find(PessimisticLockEntity.class, id, LockModeType.PESSIMISTIC_READ);
            PessimisticLockEntity pessimisticLockEntity = entityManager.find(PessimisticLockEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
//            PessimisticLockEntity pessimisticLockEntity = entityManager.find(PessimisticLockEntity.class, id, LockModeType.PESSIMISTIC_FORCE_INCREMENT);

//            PesmisticLockEntity pesmisticLockEntity = entityManager.find(PesmisticLockEntity.class, id);
            pessimisticLockEntity.setSum(pessimisticLockEntity.getSum() + 1);
            transaction.commit();
        } catch (Throwable t) {
            t.printStackTrace();
            transaction.rollback();
        } finally {
            entityManager.close();
        }
    }

    private void pessimisticInsert(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            PessimisticLockEntity lock = new PessimisticLockEntity();
            lock.setId(id);
            lock.setSum(0);
            lock.setVersion(0);
            entityManager.persist(lock);
            transaction.commit();
        } catch (Throwable t) {
            log.error("error", t);
            transaction.rollback();
        } finally {
            entityManager.close();
        }
    }

    //    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void optimisticInsert(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            OptimisticLockEntity lock = new OptimisticLockEntity();
            lock.setId(id);
            lock.setSum(0);
            lock.setVersion(0);
            entityManager.persist(lock);
            transaction.commit();
        } catch (Throwable t) {
            transaction.rollback();
        } finally {
            entityManager.close();
        }
    }


    void optimisticSimpleUp(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            OptimisticLockEntity optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id);
            optimisticLockEntity.setSum(optimisticLockEntity.getSum() + 1);
//            optimisticLockEntity.setSum(100);
//            entityManager.persist(optimisticLockEntity);

            transaction.commit();
        } catch (Throwable t) {
            t.printStackTrace();
            transaction.rollback();
        } finally {
            entityManager.close();
        }
    }

    void optimisticUp(long id) {
        while (true) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();

//                OptimisticLockEntity optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id, LockModeType.WRITE);
//                OptimisticLockEntity optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id, LockModeType.READ);
                OptimisticLockEntity optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                OptimisticLockEntity optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id, LockModeType.OPTIMISTIC);
                optimisticLockEntity.setSum(optimisticLockEntity.getSum() + 1);
                transaction.commit();
                break;
            } catch (Throwable t) {
                t.printStackTrace();
                transaction.rollback();
            } finally {
                entityManager.close();
            }
        }
    }

    @Test
    void pesmisticLock() {

    }
}