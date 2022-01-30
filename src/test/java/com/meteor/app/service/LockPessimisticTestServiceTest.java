package com.meteor.app.service;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        lockTestService.insertPessimisticLockEntity(pessimisticLockEntity);
        lockTestService.selectUpdateSumOrderTest(pessimisticLockEntity.getId());
    }

}
