package com.meteor.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meteor.app.entity.lock.OptimisticLockEntity;

public interface OptimisticLockRepository extends JpaRepository<OptimisticLockEntity, Long> {

//    @Query("update OptimisticLockEntity set sum = :sum where id=:id")
//    Long updateByIdForSum(Long id, Long sum);

}
