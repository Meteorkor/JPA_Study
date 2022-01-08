package com.meteor.app;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.RollbackException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.meteor.app.entity.lock.OptimisticLockEntity;
import com.meteor.app.entity.lock.PessimisticLockEntity;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class LockTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private final int expectSum = 1000;
    private long id = 14;

    @BeforeEach
    void beforeEach() {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            OptimisticLockEntity optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id);
            if (optimisticLockEntity != null) {
                entityManager.remove(optimisticLockEntity);
            }
            transaction.commit();
        } catch (RollbackException t) {
            t.printStackTrace();
            transaction.rollback();
        } finally {
            entityManager.close();
        }
    }

    @Test
    void transferTest() {
        int defaultSum = 10;
        int transferCnt = 4;
        long fromId = ThreadLocalRandom.current().nextInt(1,1000);
        long toId = ThreadLocalRandom.current().nextInt(1000,2000);
        optimisticInsert(fromId, defaultSum);
        optimisticInsert(toId, defaultSum);

        IntStream.range(0, transferCnt).forEach(n -> transfer(fromId, toId, 1));

        {
            final OptimisticLockEntity lockEntity = findId(fromId);
            Assertions.assertEquals(defaultSum - transferCnt, lockEntity.getSum());
        }
        {
            final OptimisticLockEntity lockEntity = findId(toId);
            Assertions.assertEquals(defaultSum + transferCnt, lockEntity.getSum());
        }
    }

    @Test
    void transferConcurrentTest() throws InterruptedException {
        long defaultSum = 10000;
        int callCnt = 100;
        long fromId = ThreadLocalRandom.current().nextInt(1,1000);
        long toId = ThreadLocalRandom.current().nextInt(1000,2000);
        optimisticInsert(fromId, defaultSum);
        optimisticInsert(toId, defaultSum);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.invokeAll(
                IntStream.range(0, callCnt).mapToObj(n -> {
                    return new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            transfer(fromId, toId, 1);
                            transfer(toId, fromId, 1);
                            transfer(fromId, toId, 1);
                            return "";
                        }
                    };
                }).collect(Collectors.toList())
        );

        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);

        {
            final OptimisticLockEntity lockEntity = findId(fromId);
            Assertions.assertEquals(defaultSum - callCnt, lockEntity.getSum());

        }
        {
            final OptimisticLockEntity lockEntity = findId(toId);
            Assertions.assertEquals(defaultSum + callCnt, lockEntity.getSum());
        }
    }



    @Test
    void simpleOptimisticTest() {
        optimisticInsert(id);
        {
            final OptimisticLockEntity lockEntity = findId(id);
            Assertions.assertEquals(0L, lockEntity.getVersion());
        }
        optimisticSimpleUp(id);
        {
            final OptimisticLockEntity lockEntity = findId(id);
            Assertions.assertEquals(1L, lockEntity.getVersion());
            Assertions.assertEquals(1L, lockEntity.getSum());
        }
    }

    @Test
    void simpleOptimisticForceIncrementTest() {

        optimisticInsert(id);
        {
            final OptimisticLockEntity lockEntity = findId(id);
            Assertions.assertEquals(0L, lockEntity.getVersion());
        }
        optimisticIncrementUp(id);
        {
            //data 업데이트 시 version+1
            //data 업데이트 후 version+1
            final OptimisticLockEntity lockEntity = findId(id);
            Assertions.assertEquals(2L, lockEntity.getVersion());
            Assertions.assertEquals(1L, lockEntity.getSum());
        }
    }

    //    @Test
    void simplePessimisticUpTest() {

        optimisticInsert(id);
        {
            final OptimisticLockEntity lockEntity = findId(id);
            System.out.println("lockEntity : " + lockEntity);
            Assertions.assertEquals(0L, lockEntity.getVersion());
        }
        pessimisticUp(id);
        {
            final OptimisticLockEntity lockEntity = findId(id);
            System.out.println("lockEntity : " + lockEntity);
            Assertions.assertEquals(0L, lockEntity.getVersion());
            Assertions.assertEquals(1L, lockEntity.getSum());
        }

    }

    @Test
    void localLockTest() throws InterruptedException {
        optimisticInsert(id);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.invokeAll(
                IntStream.range(0, expectSum).mapToObj(n -> {
                    return new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            synchronized (EntityManagerFactory.class) {
                                optimisticSimpleUp(id);
                            }
                            return "";
                        }
                    };
                }).collect(Collectors.toList())
        );

        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);

        OptimisticLockEntity optimisticLockEntity = findId(id);
        Assertions.assertEquals(expectSum, optimisticLockEntity.getSum());
    }

    @Test
    void optimisticLockTest() throws InterruptedException {
        optimisticInsert(id);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.invokeAll(
                IntStream.range(0, expectSum).mapToObj(n -> {
                    return new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            optimisticTryWhileUp(id);
                            return "";
                        }
                    };
                }).collect(Collectors.toList())
        );
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        OptimisticLockEntity optimisticLockEntity = findId(id);
        Assertions.assertEquals(expectSum, optimisticLockEntity.getSum());
    }

    @Test
    void optimisticIncCountUpLock() throws InterruptedException {
        optimisticInsert(id);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < expectSum; i++) {
            executorService.submit(() -> {
                optimisticUp(id);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        OptimisticLockEntity optimisticLockEntity = findId(id);
        Assertions.assertEquals(expectSum, optimisticLockEntity.getSum());
    }

    @Test
    void pessimisticLock() throws InterruptedException {
        pessimisticInsert(id);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < expectSum; i++) {
            executorService.submit(() -> {
                pessimisticUp(id);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        PessimisticLockEntity pessimisticLockEntity = findPessimisticLockById(id);
        Assertions.assertEquals(expectSum, pessimisticLockEntity.getSum());
    }

    private void pessimisticUp(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            PessimisticLockEntity pessimisticLockEntity = entityManager.find(PessimisticLockEntity.class, id,
                                                                             LockModeType.PESSIMISTIC_WRITE);
            System.out.println(id + "]pessimisticLockEntity : " + pessimisticLockEntity);
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
        optimisticInsert(id, 0);
    }

    void optimisticInsert(long id, long sum) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            OptimisticLockEntity lock = new OptimisticLockEntity();
            lock.setId(id);
            lock.setSum(sum);
            lock.setVersion(0);
            entityManager.persist(lock);
            transaction.commit();
        } catch (Throwable t) {
            transaction.rollback();
        } finally {
            entityManager.close();
        }
    }

    void optimisticTryWhileUp(long id) {
        while (!optimisticSimpleUp(id)) ;
    }

    boolean optimisticSimpleUp(long id) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            OptimisticLockEntity optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id);
            optimisticLockEntity.setSum(optimisticLockEntity.getSum() + 1);
            transaction.commit();
            return true;
        } catch (RollbackException t) {
            t.printStackTrace();
            transaction.rollback();
        } finally {
            entityManager.close();
        }
        return false;
    }

    boolean optimisticIncrementUp(long id) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            OptimisticLockEntity optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id,
                                                                           LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            optimisticLockEntity.setSum(optimisticLockEntity.getSum() + 1);
            transaction.commit();
            return true;
        } catch (RollbackException t) {
            t.printStackTrace();
            transaction.rollback();
        } finally {
            entityManager.close();
        }
        return false;
    }

    private OptimisticLockEntity findId(long id) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        OptimisticLockEntity optimisticLockEntity = null;
        try {
            optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id);
        } finally {
            entityManager.close();
        }
        return optimisticLockEntity;
    }

    private PessimisticLockEntity findPessimisticLockById(long id) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        PessimisticLockEntity pessimisticLockEntity = null;
        try {
            pessimisticLockEntity = entityManager.find(PessimisticLockEntity.class, id);
        } finally {
            entityManager.close();
        }
        return pessimisticLockEntity;
    }

    void optimisticUp(long id) {
        while (true) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                OptimisticLockEntity optimisticLockEntity = entityManager.find(OptimisticLockEntity.class, id,
                                                                               LockModeType.OPTIMISTIC_FORCE_INCREMENT);
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

    void transfer(long from, long to, long value) {
        while (true) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                OptimisticLockEntity optimisticLockFromEntity = entityManager.find(OptimisticLockEntity.class,
                                                                                   from,
                                                                                   LockModeType.PESSIMISTIC_WRITE);
                OptimisticLockEntity optimisticLockToEntity = entityManager.find(OptimisticLockEntity.class, to,
                                                                                 LockModeType.PESSIMISTIC_WRITE);

                if (optimisticLockFromEntity.getSum() >= value) {
                    optimisticLockFromEntity.setSum(optimisticLockFromEntity.getSum() - value);
                    optimisticLockToEntity.setSum(optimisticLockToEntity.getSum() + value);
                }
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
}