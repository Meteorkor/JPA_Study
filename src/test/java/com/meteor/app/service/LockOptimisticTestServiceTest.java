package com.meteor.app.service;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import com.meteor.app.entity.lock.OptimisticLockEntity;

//@DataJpaTest
//@Import(LockTestService.class)
@SpringBootTest
public class LockOptimisticTestServiceTest {

    @Autowired
    private LockOptimisticTestService lockTestService;

    @Test
    void simpleQueryTest() {
        OptimisticLockEntity optimisticLockEntity = new OptimisticLockEntity();
        optimisticLockEntity.setSum(1);

        final int expectSum = 22;
        lockTestService.insertOptimisticLock(optimisticLockEntity);

        lockTestService.updateSum(optimisticLockEntity.getId(), expectSum);

        Optional<OptimisticLockEntity> byId = lockTestService.findById(optimisticLockEntity.getId());
        Assertions.assertThat(byId.get().getSum()).isEqualTo(expectSum);
    }

    @Test
    void selectUpdateSumOrderTest() {
        OptimisticLockEntity optimisticLockEntity = new OptimisticLockEntity();
        optimisticLockEntity.setSum(1);
        lockTestService.insertOptimisticLock(optimisticLockEntity);
        lockTestService.selectUpdateSumOrderTest(optimisticLockEntity.getId());
    }

    @Test
    void selectImmediateUpdateSumOrderTest() {
        OptimisticLockEntity optimisticLockEntity = new OptimisticLockEntity();
        optimisticLockEntity.setSum(1);
        lockTestService.insertOptimisticLock(optimisticLockEntity);
        lockTestService.selectImmediateUpdateSumOrderTest(optimisticLockEntity.getId());
    }

    /**
     * Thread 1----SELECT------prepare.countDown()--delayTime------------------UPDATE---org.hibernate.StaleStateException
     * Thread 2-------------------------------------SELECT-----UPDATE---COMMIT
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    void optimisticNotFlushTest() throws InterruptedException, ExecutionException {
        OptimisticLockEntity optimisticLockEntity = new OptimisticLockEntity();
        optimisticLockEntity.setSum(1);
        lockTestService.insertOptimisticLock(optimisticLockEntity);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        long delayTime = 1000;
        CountDownLatch prepare = new CountDownLatch(1);

        executorService.submit(() -> {
                                   lockTestService.optimisticLockFunction(optimisticLockRepository -> {
                                       try {
                                           Optional<OptimisticLockEntity> byId =
                                                   optimisticLockRepository.findById(optimisticLockEntity.getId());
                                           byId.ifPresent(lockEntity -> lockEntity.setSum(10));
                                           prepare.countDown();
                                           Thread.sleep(delayTime);
                                       } catch (Throwable t) {
                                           t.printStackTrace();
                                       }
                                       return null;
                                   });
                               }
        );
        Future<?> submit = executorService.submit(
                () -> lockTestService.optimisticLockFunction(optimisticLockRepository -> {
                    StopWatch stopWatch = new StopWatch();
                    try {
                        prepare.await(1, TimeUnit.SECONDS);
                        stopWatch.start();
                        Optional<OptimisticLockEntity> byId =
                                optimisticLockRepository.findById(
                                        optimisticLockEntity.getId());
                        prepare.countDown();
                        byId.ifPresent(lockEntity -> lockEntity.setSum(10));
                        stopWatch.stop();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    return stopWatch.getTotalTimeMillis();
                })
        );

        Long lateSelectForUpdateTime = (Long) submit.get();
        Assertions.assertThat(lateSelectForUpdateTime).isLessThan(delayTime);//less

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    /**
     * Thread 1----SELECT---UPDATE(FLUSH)---prepare.countDown()--delayTime-------------------COMMIT-----
     * Thread 2------------------------------------------------SELECT-----UPDATE(WAIT Thread1 UPDATE)---org.hibernate.StaleStateException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    void optimisticFlushTest() throws InterruptedException, ExecutionException {
        OptimisticLockEntity optimisticLockEntity = new OptimisticLockEntity();
        optimisticLockEntity.setSum(1);
        lockTestService.insertOptimisticLock(optimisticLockEntity);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        long delayTime = 1000;
        CountDownLatch prepare = new CountDownLatch(1);

        executorService.submit(() -> {
                                   lockTestService.optimisticLockFunction(optimisticLockRepository -> {
                                       try {
                                           Optional<OptimisticLockEntity> byId =
                                                   optimisticLockRepository.findById(optimisticLockEntity.getId());
                                           byId.ifPresent(lockEntity -> lockEntity.setSum(10));
                                           optimisticLockRepository.flush();
                                           //SecondThread will waiting
                                           prepare.countDown();
                                           Thread.sleep(delayTime);
                                       } catch (Throwable t) {
                                           t.printStackTrace();
                                       }
                                       return null;
                                   });
                               }
        );

        //SecondThread
        Future<?> submit = executorService.submit(
                () -> lockTestService.optimisticLockFunction(optimisticLockRepository -> {
                    StopWatch stopWatch = new StopWatch();
                    try {
                        prepare.await(1, TimeUnit.SECONDS);
                        stopWatch.start();
                        System.out.println("2222============");
                        Optional<OptimisticLockEntity> byId =
                                optimisticLockRepository.findById(
                                        optimisticLockEntity.getId());
                        byId.ifPresent(lockEntity -> lockEntity.setSum(10));
                        stopWatch.stop();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    return stopWatch.getTotalTimeMillis();
                })
        );
        Assertions.assertThatExceptionOfType(ExecutionException.class)
                  .isThrownBy(submit::get);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}
