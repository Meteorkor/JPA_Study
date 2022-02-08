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
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.StopWatch;

import com.meteor.app.entity.lock.PessimisticLockEntity;

//@DataJpaTest
//@Import(LockTestService.class)
@SpringBootTest
public class LockPessimisticTestServiceTest {

    @Autowired
    private LockPessimisticTestService lockTestService;

    @Test
    void selectUpdateSumOrderTest() {
        PessimisticLockEntity pessimisticLockEntity = new PessimisticLockEntity();
        pessimisticLockEntity.setSum(1);
        {
            lockTestService.insertPessimisticLockEntity(pessimisticLockEntity);
            Optional<PessimisticLockEntity> byId = lockTestService.findById(pessimisticLockEntity.getId());
            Assertions.assertThat(byId.orElseThrow().getSum()).isEqualTo(1);
        }
        {
            lockTestService.selectUpdateSumOrderTest(pessimisticLockEntity.getId());
            Optional<PessimisticLockEntity> byId = lockTestService.findById(pessimisticLockEntity.getId());
            Assertions.assertThat(byId.orElseThrow().getSum()).isEqualTo(30);
        }
    }

    @Test
    void selectUpdateSecondWaitTest() throws InterruptedException, ExecutionException {
        PessimisticLockEntity pessimisticLockEntity = new PessimisticLockEntity();
        pessimisticLockEntity.setSum(1);
        lockTestService.insertPessimisticLockEntity(pessimisticLockEntity);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        long delayTime = 1000;

        CountDownLatch prepare = new CountDownLatch(1);

        executorService.submit(() -> {
                                   lockTestService.pessimisticLockFunction(pessimisticLockRepository -> {
                                       try {
                                           Optional<PessimisticLockEntity> byIdForUpdate =
                                                   pessimisticLockRepository.findByIdForUpdate(
                                                           pessimisticLockEntity.getId());
                                           prepare.countDown();
                                           byIdForUpdate.ifPresent(lockEntity -> lockEntity.setSum(10));
                                           Thread.sleep(delayTime);
                                       } catch (Throwable t) {
                                           t.printStackTrace();
                                       }
                                       return null;
                                   });
                               }
        );
        Future<?> submit = executorService.submit(
                () -> lockTestService.pessimisticLockFunction(pessimisticLockRepository -> {
                    StopWatch stopWatch = new StopWatch();
                    try {
                        prepare.await(1, TimeUnit.SECONDS);
                        stopWatch.start();
                        Optional<PessimisticLockEntity> byIdForUpdate =
                                pessimisticLockRepository.findByIdForUpdate(
                                        pessimisticLockEntity.getId());
                        byIdForUpdate.ifPresent(lockEntity -> lockEntity.setSum(10));
                        stopWatch.stop();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    return stopWatch.getTotalTimeMillis();
                })
        );

        Long lateSelectForUpdateTime = (Long) submit.get();
        Assertions.assertThat(lateSelectForUpdateTime).isGreaterThan(delayTime);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}
