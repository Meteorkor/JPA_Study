package com.meteor.app.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.meteor.app.entity.lock.OptimisticLockEntity;

//@DataJpaTest
//@Import(LockTestService.class)
@SpringBootTest
public class LockTestServiceTest {

    @Autowired
    private LockTestService lockTestService;

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
}
