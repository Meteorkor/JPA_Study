package com.meteor.app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meteor.app.entity.lock.PessimisticLockEntity;
import com.meteor.app.repo.PessimisticLockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LockPessimisticTestService {
    private final PessimisticLockRepository pessimisticLockRepository;
    private final ItemService itemServiceForTest;//for test

    @Transactional
    public void selectUpdateSum(long id, long sum) {
        Optional<PessimisticLockEntity> byId = pessimisticLockRepository.findByIdForUpdate(id);
        byId.ifPresent(entity -> {
            entity.setSum(sum);
        });
    }

    @Transactional
    public void insertPessimisticLockEntity(PessimisticLockEntity pessimisticLockEntity) {
        pessimisticLockRepository.save(pessimisticLockEntity);
    }

    @Transactional
    public void selectUpdateSumOrderTest(long id) {
        selectUpdateSum(id, 30);
        itemServiceForTest.findItem(id);
        /**
         * query execution order
         * 1. selectUpdateSum, findById
         * 2. itemServiceForTest.findItem
         * 3. selectUpdateSum, update(entity.setSum(sum))
         */
    }

//    @Transactional
//    public void selectImmediateUpdateSumOrderTest(long id) {
//        selectImmediateUpdateSum(id, 30);
//        itemServiceForTest.findItem(id);
//        /**
//         * query execution order
//         * 1. selectImmediateUpdateSum, findById
//         * 2. selectImmediateUpdateSum(saveAndFlush), update
//         * 3. itemServiceForTest.findItem
//         */
//    }
}
