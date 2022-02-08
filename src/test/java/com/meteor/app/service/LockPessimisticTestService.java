package com.meteor.app.service;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.meteor.app.entity.lock.PessimisticLockEntity;
import com.meteor.app.repo.PessimisticLockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LockPessimisticTestService {
    private final PessimisticLockRepository pessimisticLockRepository;
    private final ItemService itemServiceForTest;//for test

    public Optional<PessimisticLockEntity> findById(long id) {
        return pessimisticLockRepository.findById(id);
    }

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
        selectUpdateSumOrderTest(id, null);
        /**
         * query execution order
         * 1. selectUpdateSum, findById
         * 2. itemServiceForTest.findItem
         * 3. selectUpdateSum, update(entity.setSum(sum))
         */
    }

    @Transactional
    public void selectUpdateSumOrderTest(long id, Runnable callBack) {
        selectUpdateSum(id, 30);
        itemServiceForTest.findItem(id);
        Optional.ofNullable(callBack).ifPresent(Runnable::run);
        /**
         * query execution order
         * 1. selectUpdateSum, findById
         * 2. itemServiceForTest.findItem
         * 3. selectUpdateSum, update(entity.setSum(sum))
         */
    }

    @Transactional
    public Object pessimisticLockFunction(Function<PessimisticLockRepository, Object> function) {
        return function.apply(pessimisticLockRepository);
    }
}
