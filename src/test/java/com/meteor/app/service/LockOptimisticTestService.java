package com.meteor.app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meteor.app.entity.Item;
import com.meteor.app.entity.lock.OptimisticLockEntity;
import com.meteor.app.repo.OptimisticLockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LockOptimisticTestService {
    private final OptimisticLockRepository optimisticLockRepository;
    private final ItemService itemServiceForTest;//for test

    public Optional<OptimisticLockEntity> findById(long id) {
        return optimisticLockRepository.findById(id);
    }

    @Transactional
    public OptimisticLockEntity insertOptimisticLock(OptimisticLockEntity optimisticLockEntity) {
        return optimisticLockRepository.save(optimisticLockEntity);
    }

    @Transactional
    public void updateSum(long id, long sum) {
        Optional<OptimisticLockEntity> byId = findById(id);
        byId.ifPresent(entity -> {
            entity.setSum(sum);
        });
    }

    @Transactional
    public void selectUpdateSum(long id, long sum) {
        Optional<OptimisticLockEntity> byId = findById(id);
        byId.ifPresent(entity -> {
            entity.setSum(sum);
        });
    }

    @Transactional
    public void selectImmediateUpdateSum(long id, long sum) {
        Optional<OptimisticLockEntity> byId = findById(id);
        byId.ifPresent(entity -> {
            entity.setSum(sum);
            optimisticLockRepository.saveAndFlush(entity);
        });
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

    @Transactional
    public void selectImmediateUpdateSumOrderTest(long id) {
        selectImmediateUpdateSum(id, 30);
        itemServiceForTest.findItem(id);
        /**
         * query execution order
         * 1. selectImmediateUpdateSum, findById
         * 2. selectImmediateUpdateSum(saveAndFlush), update
         * 3. itemServiceForTest.findItem
         */
    }

}
