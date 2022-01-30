package com.meteor.app.repo;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.meteor.app.entity.lock.PessimisticLockEntity;

public interface PessimisticLockRepository extends JpaRepository<PessimisticLockEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PessimisticLockEntity p where id=:id")
    Optional<PessimisticLockEntity> findByIdForUpdate(Long id);
}
