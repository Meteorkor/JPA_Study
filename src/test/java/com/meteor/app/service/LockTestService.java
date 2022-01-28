package com.meteor.app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meteor.app.entity.lock.OptimisticLockEntity;
import com.meteor.app.repo.OptimisticLockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LockTestService {
    private final OptimisticLockRepository optimisticLockRepository;
//    private final ItemService itemServiceForTest;//for test

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

//    @Transactional
//    public void otherWorkForTest(long id) {
//        Optional<OptimisticLockEntity> byId = findById(id);
//        byId.ifPresent(entity -> {
//            entity.setSum(40);
//        });
//        itemServiceForTest.
//    }

}
