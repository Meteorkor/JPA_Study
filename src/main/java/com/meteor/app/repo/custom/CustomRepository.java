package com.meteor.app.repo.custom;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import com.meteor.app.entity.Book;

@RepositoryDefinition(domainClass = Book.class, idClass = Long.class)
public interface CustomRepository {
    Book save(Book book);

    Optional<Book> findById(Long id);

    @Query(value = "select * from Item where item_id=?1", nativeQuery = true)
    Optional<Book> findByIdCustom(Long id);

    @Query(value = "select * from Item", nativeQuery = true, countQuery = "select count(*) from item")
    Page<Book> findAllPaging(Pageable pageable);
}
