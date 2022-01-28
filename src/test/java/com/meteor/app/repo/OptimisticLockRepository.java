package com.meteor.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meteor.app.entity.lock.OptimisticLockEntity;

public interface OptimisticLockRepository extends JpaRepository<OptimisticLockEntity, Long> {


}
