package com.meteor.app.repo;

import java.util.Optional;

import javax.persistence.LockModeType;

import com.meteor.app.entity.Item;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepo extends CrudRepository<Item, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id=:id")
    Optional<Item> findByIdForUpdate(Long id);
}
